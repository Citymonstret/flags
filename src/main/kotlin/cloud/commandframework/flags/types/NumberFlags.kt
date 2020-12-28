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

/**
 * A flag that parses integers.
 *
 * @param number Input.
 * @param F Extending flag type.
 */
abstract class IntegerFlag<out F : IntegerFlag<F>>(number: Int) : AbstractFlag<Int, F>(number) {

    override fun parse(input: String): F {
        return try {
            flagOf(input.toInt())
        } catch (exception: NumberFormatException) {
            throw FlagParseException(this, input, "Value has to be an integer")
        }
    }

    override fun merge(input: Int) = flagOf(this.value + input)

    override fun example() = "10"

    override fun toString() = value.toString()
}

/**
 * A flag that parses doubles
 *
 * @param number Input
 * @param F Extending flag type
 */
abstract class DoubleFlag<out F : DoubleFlag<F>>(number: Double) : AbstractFlag<Double, F>(number) {

    override fun parse(input: String): F {
        return try {
            flagOf(input.toDouble())
        } catch (exception: NumberFormatException) {
            throw FlagParseException(this, input, "Value has to be an decimal number")
        }
    }

    override fun merge(input: Double) = flagOf(this.value + input)

    override fun example() = "10.0"

    override fun toString() = value.toString()
}
