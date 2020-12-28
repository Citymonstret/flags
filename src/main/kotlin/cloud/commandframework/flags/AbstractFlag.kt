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

/**
 * A flag is any property that can be assigned to an object, that will later alter its functionality
 * in some way.
 *
 * @param T Value type.
 * @param F The flag type itself.
 * @param value The flag value.
 */
abstract class AbstractFlag<T : Any, out F : AbstractFlag<T, F>>
    protected constructor(val value: T) {

    /** The flag name. This is constructed from the class name. */
    val name: String

    init {
        val builder = StringBuilder()
        val chars =
            requireNotNull(this::class.simpleName) { "flag class name is null?" }
                .replace("Flag", "")
                .toCharArray()
        for (i in chars.indices) {
            builder.append(
                when {
                    i == 0 -> chars[i].toLowerCase().toString()
                    chars[i].isUpperCase() -> "-${chars[i].toLowerCase()}"
                    else -> chars[i].toString()
                })
        }
        name = builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AbstractFlag<*, *>
        if (value != other.value) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    /**
     * Serialize the flag value.
     *
     * @return Flag value in a format that would parse an identical flag instance.
     */
    abstract fun serialize(): String

    override fun toString() = serialize()

    /**
     * Parse the string [input] into a flag, and throw an exception in the case that the string does
     * not represent a valid flag value. This instance won't change its state, but instead an
     * instance holding the parsed flag value will be returned.
     *
     * @param input The string input that will be parsed in the flag value.
     * @return The flag containing the parsed value.
     * @throws FlagParseException If the value could not be parsed.
     */
    abstract fun parse(input: String): F

    /**
     * Merge this flag's value with the [input] value and return a new flag instance containing the
     * merged values.
     *
     * @param input The value to add.
     * @return Flag containing the values.
     */
    abstract fun merge(input: T): F

    /**
     * Get an example string that would result in a correctly parsed value.
     *
     * @return An example flag value.
     */
    abstract fun example(): String

    /**
     * Create a new flag instance with the given [value]
     *
     * @param value Flag value.
     */
    protected abstract fun flagOf(value: T): F

    /**
     * Create a new flag instance with the given [value].
     *
     * @param value Flag value.
     */
    fun createFlagInstance(value: T) = flagOf(value)
}
