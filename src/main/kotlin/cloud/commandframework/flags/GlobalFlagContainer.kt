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

/** The global flag container. */
object GlobalFlagContainer : FlagContainer(null) {

    private val stringClassMap = mutableMapOf<String, KClass<*>>()

    init {
        this.subscribe { flag, type ->
            if (type == FlagUpdateType.FLAG_ADDED) {
                this.stringClassMap.put(flag.name.toLowerCase(), flag::class)
            }
        }
    }

    override operator fun <V, T : AbstractFlag<out V, *>> get(flagClass: KClass<T>): T? {
        val flag = super.get(flagClass)
        if (flag != null) {
            return flag
        }
        throw IllegalStateException(
            "Unrecognized flag '${flagClass.simpleName}'. All flag types must be present in the global flag container.")
    }

    override fun getFlagErased(flagClass: KClass<*>): AbstractFlag<*, *> {
        val flag = super.getFlagErased(flagClass)
        if (flag != null) {
            return flag
        }
        throw IllegalStateException(
            "Unrecognized flag '${flagClass.simpleName}'. All flag types must be present in the global flag container.")
    }

    /**
     * Get a flag class from its name.
     *
     * @param name Flag name.
     */
    fun flagClassFromString(name: String): KClass<*>? = this.stringClassMap[name.toLowerCase()]

    /**
     * Get a flag instance from its name.
     *
     * @param name Flag name.
     */
    fun flagFromName(name: String) = this.getFlagErased(flagClassFromString(name)!!)
}
