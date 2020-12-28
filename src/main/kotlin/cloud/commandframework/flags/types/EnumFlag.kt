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

package cloud.commandframework.flags.types

import cloud.commandframework.flags.AbstractFlag
import cloud.commandframework.flags.FlagParseException
import kotlin.reflect.KClass

/**
 * Flag implementation dealing with enums.
 *
 * @param clazz Enum class.
 * @param enum Default value.
 * @param E Enum type.
 */
abstract class EnumFlag<E : Enum<E>>(clazz: KClass<E>, enum: E) :
    AbstractFlag<E, EnumFlag<E>>(enum) {

    private val values = clazz.java.enumConstants as Array<E>

    override fun serialize() = value.name

    override fun parse(input: String): EnumFlag<E> {
        for (e in values) {
            if (e.name.equals(input, true)) {
                return flagOf(e)
            }
        }
        throw FlagParseException(
            this,
            input,
            buildString {
                val iterator = values.iterator()
                while (iterator.hasNext()) {
                    append(iterator.next())
                    if (iterator.hasNext()) {
                        append(',')
                    }
                }
            })
    }

    override fun merge(input: E): EnumFlag<E> = flagOf(value)

    override fun example() = this.values[0].name
}
