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

import kotlin.reflect.KClass

/**
 * Any object that is able to provide access to [AbstractFlag] instances
 */
interface FlagHolder {

    /** All flags stored in this container. */
    val flagMap: Map<KClass<*>, AbstractFlag<*, *>>

    /**
     * All flags recognized by this flag container. This will by default use the flags known by the
     * highest container in the container hierarchy.
     */
    val recognizedFlags: Collection<AbstractFlag<*, *>>

    /**
     * Has the same functionality as get, but with wildcard generic types.
     *
     * @param flagClass The flag class.
     */
    fun getFlagErased(flagClass: KClass<*>): AbstractFlag<*, *>?

    /**
     * Check for flag existence in this flag container.
     *
     * @param flagClass Flag class to query for.
     * @param V Flag value type.
     * @param T Flag type.
     * @return The flag instance if it exists, else null.
     */
    fun <V, T : AbstractFlag<out V, *>> queryLocal(flagClass: KClass<T>): T?

    /**
     * Query all levels of flag containers for a flag. This guarantees that a flag instance is
     * returned, as long as it is registered in the global flag container.
     *
     * @param flagClass Flag class to query for
     * @param V Flag value type
     * @param T Flag type
     * @return Flag instance
     */
    operator fun <V, T : AbstractFlag<out V, *>> get(flagClass: KClass<T>): T

}
