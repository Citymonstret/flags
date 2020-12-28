/*
 * MIT License
 *
 * Copyright (c) 2020 Alexander Söderberg & Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cloud.commandframework.flags

import java.lang.IllegalStateException
import kotlin.reflect.KClass

/**
 * Container of [AbstractFlag] instances. Default values are inherited from the parent container. At
 * the top of the parent-child hierarchy must be a [GlobalFlagContainer](or equivalent).
 *
 * @param parentContainer The parent container. The top level flag container should not have a
 * parent container, and can set this parameter to null. If this is not a top level flag container,
 * the parent should never be null.
 */
open class FlagContainer(var parentContainer: FlagContainer?) {

    private val _unknownFlags = mutableMapOf<String, String>()
    private val _flagMap = mutableMapOf<KClass<*>, AbstractFlag<*, *>>()
    private val _updateSubscribers = mutableListOf<(AbstractFlag<*, *>, FlagUpdateType) -> Unit>()

    /** All flags stored in this container. */
    val flagMap: Map<KClass<*>, AbstractFlag<*, *>>
        get() = this._flagMap.toMap()

    /** The container highest up in the parent hierarchy. */
    val highestFlagContainer: FlagContainer
        get() = this.parentContainer?.highestFlagContainer ?: this

    /**
     * All flags recognized by this flag container. This will by default use the flags known by the
     * highest container in the container hierarchy.
     */
    val recognizedFlags: Collection<AbstractFlag<*, *>>
        get() = this.highestFlagContainer.flagMap.values

    /**
     * Query all levels of flag containers for a flag. This guarantees that a flag instance is
     * returned, as long as it is registered in the global flag container.
     *
     * @param flagClass Flag class to query for
     * @param V Flag value type
     * @param T Flag type
     * @return Flag instance
     */
    open operator fun <V, T : AbstractFlag<out V, *>> get(flagClass: KClass<T>): T {
        val flag = this._flagMap[flagClass]
        if (flag != null) {
            return flag as T
        } else if (this.parentContainer != null) {
            return this.parentContainer!![flagClass]
        } else {
            throw IllegalStateException("Could not find flag of type '${flagClass.simpleName}'")
        }
    }

    /**
     * Subscribe to flag updates in this particular flag container instance. Updates are: a flag
     * being removed; a flag being added; or a flag being updated.
     *
     * @param flagUpdateHandler The update handler which will react to changes.
     */
    fun subscribe(flagUpdateHandler: (AbstractFlag<*, *>, FlagUpdateType) -> Unit) {
        this._updateSubscribers += flagUpdateHandler
    }

    /**
     * Add a new flag to the flag container.
     *
     * @param flag The flag to add.
     */
    operator fun <V, T : AbstractFlag<out V, *>> plusAssign(flag: T) {
        this.add(flag)
    }

    /**
     * Add a new flag to the flag container.
     *
     * @param flag The flag to add.
     */
    fun <V, T : AbstractFlag<out V, *>> add(flag: T) {
        val oldInstance = this._flagMap.put(flag::class, flag)
        val flagUpdateType =
            if (oldInstance == null) FlagUpdateType.FLAG_ADDED else FlagUpdateType.FLAG_UPDATED
        this._updateSubscribers.forEach { it.invoke(flag, flagUpdateType) }
    }

    /**
     * Remove a flag from the flag container.
     *
     * @param flag The flag to remove.
     * @return The value stored in the flag, or null.
     */
    operator fun <V, T : AbstractFlag<out V, *>> minusAssign(flag: T) {
        this.remove(flag)
    }

    /**
     * Remove a flag from the flag container.
     *
     * @param flag The flag to remove.
     * @return The value stored in the flag, or null.
     */
    fun <V, T : AbstractFlag<out V, *>> remove(flag: T): V? {
        val value = this._flagMap.remove(flag::class)
        this._updateSubscribers.forEach { it.invoke(flag, FlagUpdateType.FLAG_REMOVED) }
        return value as? V
    }

    /**
     * Add all flags from a collection this flag container.
     *
     * @param flags Flags to add.
     */
    fun addAll(flags: Collection<AbstractFlag<*, *>>) {
        flags.forEach { this.add(it) }
    }

    /** Remove all flags from this container. This does not propagate to parent containers. */
    fun clear() {
        this._flagMap.clear()
    }

    /**
     * Check for flag existence in this flag container.
     *
     * @param flagClass Flag class to query for.
     * @param V Flag value type.
     * @param T Flag type.
     * @return The flag instance if it exists, else null.
     */
    fun <V, T : AbstractFlag<out V, *>> queryLocal(flagClass: KClass<T>): T? {
        return this._flagMap[flagClass] as? T
    }

    /**
     * Has the same functionality as get, but with wildcard generic types.
     *
     * @param flagClass The flag class.
     */
    open fun getFlagErased(flagClass: KClass<*>): AbstractFlag<*, *>? {
        val flag = this._flagMap[flagClass]
        if (flag != null) {
            return flag
        } else if (this.parentContainer != null) {
            return this.parentContainer!!.getFlagErased(flagClass)
        }
        return null
    }
}

/** Update types used in [FlagUpdateHandler]. */
enum class FlagUpdateType {
    /** A flag was added to a flag container */
    FLAG_ADDED,
    /** A flag was removed from a flag container */
    FLAG_REMOVED,
    /** A flag was already stored in this container, but a new instance has bow replaced it */
    FLAG_UPDATED
}
