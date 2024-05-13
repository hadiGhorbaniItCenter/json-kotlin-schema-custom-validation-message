/*
 * @(#) EnumValidator.kt
 *
 * json-kotlin-schema Kotlin implementation of JSON Schema
 * Copyright (c) 2020 Peter Wall
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

import ir.part.sdk.namabar.widget.generator.forms.compose.LibraryContext
import ir.part.sdk.namabar.widget.generator.R
import java.net.URI

import net.pwall.json.JSONSequence
import net.pwall.json.JSONValue
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.output.BasicErrorEntry

class EnumValidator(uri: URI?, location: JSONPointer, val array: JSONSequence<*>) :
    JSONSchema.Validator(uri, location) {

    override fun childLocation(pointer: JSONPointer): JSONPointer = pointer.child("enum")

    override fun validate(json: JSONValue?, instanceLocation: JSONPointer): Boolean {
        val instance = instanceLocation.eval(json)
        array.forEach { if (instance == it) return true }
        return false
    }

    override fun getErrorEntry(
        relativeLocation: JSONPointer,
        json: JSONValue?,
        instanceLocation: JSONPointer
    ):
            BasicErrorEntry? {
        val instance = instanceLocation.eval(json)
        array.forEach { if (instance == it) return null }
        return createBasicErrorEntry(
            relativeLocation, instanceLocation,
            LibraryContext.applicationContext.getString(R.string.validation_msg_Enum, instance.toErrorDisplay())
        )
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is EnumValidator && super.equals(other) && array == other.array

    override fun hashCode(): Int = super.hashCode() xor array.hashCode()

}
