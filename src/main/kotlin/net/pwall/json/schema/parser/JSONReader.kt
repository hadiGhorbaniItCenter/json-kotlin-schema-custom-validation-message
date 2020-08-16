/*
 * @(#) JSONReader.kt
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

package net.pwall.json.schema.parser

import java.io.File
import java.io.InputStream
import java.net.URI

import net.pwall.json.JSON
import net.pwall.json.JSONObject
import net.pwall.json.JSONValue
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.JSONSchemaException
import net.pwall.json.schema.parser.Parser.Companion.dropFragment
import net.pwall.json.schema.parser.Parser.Companion.getStringOrNull

class JSONReader(val uriResolver: (URI) -> InputStream?) {

    private val jsonCache: MutableMap<URI, JSONValue> = mutableMapOf()

    fun preLoad(filename: String) {
        preLoad(File(filename))
    }

    fun preLoad(file: File) {
        when {
            file.isDirectory -> file.listFiles()?.forEach { if (!it.name.startsWith('.')) preLoad(it) }
            file.isFile -> {
                if (file.name.endsWith(".json", ignoreCase = true))
                    JSON.parse(file)?.let {
                        jsonCache[file.toURI()] = it
                        it.cacheByURI()
                    }
            }
        }
    }

    fun readJSON(file: File): JSONValue {
        val uri = file.toURI()
        jsonCache[uri]?.let { return it }
        try {
            val json = JSON.parse(file.inputStream()) ?: throw JSONSchemaException("Schema file is null - $file")
            jsonCache[uri] = json
            json.cacheByURI()
            return json
        }
        catch (e: Exception) {
            throw JSONSchemaException("Error reading schema file - $file", e)
        }
    }

    fun readJSON(uri: URI): JSONValue {
        jsonCache[uri]?.let { return it }
        try {
            val json = JSON.parse(uriResolver(uri) ?: throw JSONSchemaException("Can't resolve name - $uri")) ?:
                    throw JSONSchemaException("Schema file is null - $uri")
            jsonCache[uri] = json
            json.cacheByURI()
            return json
        }
        catch (e: Exception) {
            throw JSONSchemaException("Error reading schema file - $uri", e)
        }
    }

    private fun JSONValue.cacheByURI() {
        if (this is JSONObject) {
            getStringOrNull(JSONPointer.root.child("\$id"))?.let {
                jsonCache[URI(it).dropFragment()] = this
            }
        }
    }

}