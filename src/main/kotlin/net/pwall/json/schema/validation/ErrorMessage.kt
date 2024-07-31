

package net.pwall.json.schema.validation

import java.net.URI

import net.pwall.json.JSONValue
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.output.BasicErrorEntry

@Suppress("EqualsOrHashCode")
class ErrorMessage(uri: URI?, location: JSONPointer,val message:JSONValue): JSONSchema.Validator(uri, location) {

    override fun childLocation(pointer: JSONPointer): JSONPointer = pointer.child("errorMessage")

    override fun validate(json: JSONValue?, instanceLocation: JSONPointer): Boolean {
        return false
    }

    override fun getErrorEntry(relativeLocation: JSONPointer, json: JSONValue?, instanceLocation: JSONPointer):
            BasicErrorEntry? {
        return   createBasicErrorEntry(relativeLocation, instanceLocation, message.toString())
    }

    override fun equals(other: Any?): Boolean = this === other || other is ErrorMessage && super.equals(other)


}
