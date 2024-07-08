/*
 * @(#) FormatValidator.kt
 *
 * json-kotlin-schema Kotlin implementation of JSON Schema
 * Copyright (c) 2020, 2021, 2024 Peter Wall
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

package net.pwall.json.schema.validation

import net.pwall.json.JSONDecimal
import net.pwall.json.JSONDouble
import net.pwall.json.JSONFloat
import net.pwall.json.JSONLong
import net.pwall.json.JSONString
import net.pwall.json.JSONValue
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.output.BasicErrorEntry
import net.pwall.json.schema.persianNumberToEnglishNumber
import net.pwall.json.validation.JSONValidation
import java.math.BigDecimal
import java.net.URI
import kotlin.math.floor

class FormatValidator(
    uri: URI?,
    location: JSONPointer,
    val checker: FormatChecker
) : JSONSchema.Validator(uri, location) {

    override fun childLocation(pointer: JSONPointer): JSONPointer = pointer.child("format")

    override fun validate(json: JSONValue?, instanceLocation: JSONPointer): Boolean {
        return checker.check(instanceLocation.eval(json))
    }

    override fun getErrorEntry(
        relativeLocation: JSONPointer,
        json: JSONValue?,
        instanceLocation: JSONPointer
    ):
            BasicErrorEntry? {
        val instance = instanceLocation.eval(json)
        return if (checker.check(instance)) null else
            checker.getBasicErrorEntry(this, relativeLocation, instanceLocation, json)
    }

    override fun equals(other: Any?): Boolean = this === other ||
            other is FormatValidator && super.equals(other) && checker == other.checker

    override fun hashCode(): Int = super.hashCode() xor checker.hashCode()

    interface FormatChecker {

        val name: String
        val msg: String?

        fun check(value: JSONValue?): Boolean

        fun getBasicErrorEntry(
            schema: JSONSchema,
            relativeLocation: JSONPointer,
            instanceLocation: JSONPointer,
            json: JSONValue?,
        ): BasicErrorEntry {
            return schema.createBasicErrorEntry(
                relativeLocation = relativeLocation,
                instanceLocation = instanceLocation,
                error = msg?.let { msg } ?: run {
                    "بررسی قالب مقدار ناموفق است : \"$name\", بود ${
                        instanceLocation.eval(json)?.toJSON()
                    }"
                },
            )
        }

    }

    object DateTimeFormatChecker : FormatChecker {

        override val name: String = "date-time"
        override val msg: String? = null

        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isDateTime(value.value)

    }

    object ExampleCustomFormatChecker : FormatChecker {

        override val name: String = "test"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || value.value == "test"

    }

    object DateFormatChecker : FormatChecker {

        override val name: String = "date"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isDate(value.value)

    }

    object PersianDateFormatChecker : FormatChecker {

        override val name: String = "persian-date"
        override val msg: String? = "تاریخ نا معتبر است"
        val pattern = "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}"

        override fun check(value: JSONValue?): Boolean {
            // Check if value is a JSONString and if it matches the pattern
            return value is JSONString && persianNumberToEnglishNumber(value.value).matches(
                Regex(
                    pattern
                )
            )
        }
    }

    object TimeFormatChecker : FormatChecker {

        override val name: String = "time"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isTime(value.value)

    }

    object DurationFormatChecker : FormatChecker {

        override val name: String = "duration"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isDuration(value.value)

    }

    object EmailFormatChecker : FormatChecker {

        override val name: String = "email"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isEmail(value.value)

    }

    object HostnameFormatChecker : FormatChecker {

        override val name: String = "hostname"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isHostname(value.value)

    }

    object IPV4FormatChecker : FormatChecker {

        override val name: String = "ipv4"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isIPV4(value.value)

    }

    object IPV6FormatChecker : FormatChecker {

        override val name: String = "ipv6"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isIPV6(value.value)

    }

    object URIFormatChecker : FormatChecker {

        override val name: String = "uri"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isURI(value.value)

    }

    object URIReferenceFormatChecker : FormatChecker {

        override val name: String = "uri-reference"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isURIReference(value.value)

    }

    object UUIDFormatChecker : FormatChecker {

        override val name: String = "uuid"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isUUID(value.value)

    }

    object JSONPointerFormatChecker : FormatChecker {

        override val name: String = "json-pointer"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isJSONPointer(value.value)

    }

    object RelativeJSONPointerFormatChecker : FormatChecker {

        override val name: String = "relative-json-pointer"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isRelativeJSONPointer(value.value)

    }

    object RegexFormatChecker : FormatChecker {

        override val name: String = "regex"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean =
            value !is JSONString || JSONValidation.isRegex(value.value)

    }


    object PersianStringChecker : FormatChecker {

        override val name: String = "persianString"
        override val msg: String? = "فقط حروف فارسی مجاز است!"
        val pattern =
            "^[\\u200C\\u0621\\u0622\\u0627\\u0623\\u0628\\u067e\\u062a\\u062b\\u062c\\u0686\\u062d\\u062e\\u062f\\u0630\\u0631\\u0632\\u0698\\u0633-\\u063a\\u0641\\u0642\\u06a9\\u06af\\u0644-\\u0646\\u0648\\u0624\\u0647\\u06cc\\u0626\\u0625\\u0671\\u0643\\u0629\\u064a\\u0649\\u06F0-\\u06F90-9\\s\\.]+\$"

        override fun check(value: JSONValue?): Boolean =
            if (value !is JSONString || value.value.isNotEmpty()) {
                value !is JSONString || (value.value.matches(pattern.toRegex()))
            } else true

    }

    object EnglishStringChecker : FormatChecker {

        override val name: String = "englishString"
        override val msg: String? = "فقط حروف انگلیسی مجاز است!"
        val pattern =
            """^[_A-z0-9]*((\s)*[_A-z0-9])*${'$'}"""

        override fun check(value: JSONValue?): Boolean =
            if (value !is JSONString || value.value.isNotEmpty()) {
                value !is JSONString || value.value.matches(pattern.toRegex())
            } else true

    }

    // Not yet implemented:
    //   idn-email
    //   idn-hostname
    //   iri
    //   iri-reference
    //   uri-template

    // Additional formats for OpenAPI: int32 and int64

    object Int64FormatChecker : FormatChecker {

        override val name: String = "int64"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean = when (value) {
            is JSONDecimal -> {
                try {
                    value.bigDecimalValue().setScale(0) in BigDecimal(Long.MIN_VALUE)..BigDecimal(
                        Long.MAX_VALUE
                    )
                } catch (e: ArithmeticException) {
                    false
                }
            }

            is JSONDouble -> {
                val doubleValue = value.value
                doubleValue == floor(doubleValue) && doubleValue in Long.MIN_VALUE.toDouble()..Long.MAX_VALUE.toDouble()
            }

            is JSONFloat -> {
                val floatValue = value.value
                floatValue == floor(floatValue) && floatValue in Long.MIN_VALUE.toFloat()..Long.MAX_VALUE.toFloat()
            }

            else -> true // includes JSONInteger, JSONLong, JSONZero
        }

    }

    object Int32FormatChecker : FormatChecker {

        override val name: String = "int32"
        override val msg: String? = null
        override fun check(value: JSONValue?): Boolean = when (value) {
            is JSONLong -> value.value in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()
            is JSONDecimal -> {
                try {
                    value.bigDecimalValue()
                        .setScale(0) in BigDecimal(Int.MIN_VALUE)..BigDecimal(Int.MAX_VALUE)
                } catch (e: ArithmeticException) {
                    false
                }
            }

            is JSONDouble -> {
                val doubleValue = value.value
                doubleValue == floor(doubleValue) && doubleValue in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()
            }

            is JSONFloat -> {
                val floatValue = value.value
                floatValue == floor(floatValue) && floatValue in Int.MIN_VALUE.toFloat()..Int.MAX_VALUE.toFloat()
            }

            else -> true // includes JSONInteger, JSONZero
        }

    }

    class NullFormatChecker(override val name: String, override val msg: String? = null) :
        FormatChecker {

        override fun check(value: JSONValue?): Boolean = true

        override fun equals(other: Any?): Boolean = this === other ||
                other is NullFormatChecker && name == other.name

        override fun hashCode(): Int = name.hashCode()

    }

    class DelegatingFormatChecker(
        override val name: String, vararg val validators: Validator,
        override val msg: String? = null
    ) :
        FormatChecker {

        override fun check(value: JSONValue?): Boolean {
            for (validator in validators)
                if (!validator.validate(value))
                    return false
            return true
        }

        override fun getBasicErrorEntry(
            schema: JSONSchema,
            relativeLocation: JSONPointer,
            instanceLocation: JSONPointer,
            json: JSONValue?,
        ): BasicErrorEntry {
            for (validator in validators)
                validator.getErrorEntry(relativeLocation.child(name), json, instanceLocation)
                    ?.let { return it }
            return super.getBasicErrorEntry(schema, relativeLocation, instanceLocation, json)
        }

        override fun equals(other: Any?): Boolean = this === other ||
                other is DelegatingFormatChecker && name == other.name && validators.contentEquals(
            other.validators
        )

        override fun hashCode(): Int = name.hashCode() xor validators.hashCode()

    }

    companion object {

        private val checkers = listOf(
            ExampleCustomFormatChecker,
            DateTimeFormatChecker,
            DateFormatChecker,
            PersianDateFormatChecker,
            TimeFormatChecker,
            DurationFormatChecker,
            EmailFormatChecker,
            HostnameFormatChecker,
            IPV4FormatChecker,
            IPV6FormatChecker,
            URIFormatChecker,
            URIReferenceFormatChecker,
            UUIDFormatChecker,
            JSONPointerFormatChecker,
            RelativeJSONPointerFormatChecker,
            RegexFormatChecker,
            PersianStringChecker,
            EnglishStringChecker,
            Int32FormatChecker,
            Int64FormatChecker
        )

        fun findChecker(keyword: String): FormatChecker? = checkers.find { it.name == keyword }

    }

}
