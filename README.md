# json-kotlin-schema

Kotlin implementation of JSON Schema

## Quick Start

Given the following schema file (Taken from the [Wikipedia article on JSON](https://en.wikipedia.org/wiki/JSON)):
```json
{
  "$schema": "http://json-schema.org/draft/2019-09/schema",
  "$id": "http://pwall.net/test",
  "title": "Product",
  "type": "object",
  "required": ["id", "name", "price"],
  "properties": {
    "id": {
      "type": "number",
      "description": "Product identifier"
    },
    "name": {
      "type": "string",
      "description": "Name of the product"
    },
    "price": {
      "type": "number",
      "minimum": 0
    },
    "tags": {
      "type": "array",
      "items": {
        "type": "string"
      }
    },
    "stock": {
      "type": "object",
      "properties": {
        "warehouse": {
          "type": "number"
        },
        "retail": {
          "type": "number"
        }
      }
    }
  }
}
```
and this JSON (from the same article):
```json
{
  "id": 1,
  "name": "Foo",
  "price": 123,
  "tags": [
    "Bar",
    "Eek"
  ],
  "stock": {
    "warehouse": 300,
    "retail": 20
  }
}
```
the following code will validate that the JSON matches the scheam:
```kotlin
    val schema = JSONSchema.parse("/path/to/example.schema.json")
    val json = File("/path/to/example.json").readText()
    require(schema.validate(json))
```

## Implemented Subset

This implementation does not implement the full JSON Schema specification.
The currently implemented subset includes:

### Core

- `$schema`
- `$id`
- `$defs`
- `$comment`
- `title`
- `description`

### Structure

- `properties`
- `items`
- `allOf`
- `anyOf`
- `oneOf`
- `if`
- `then`
- `else`
- `default`

### Validation

- `type` (`null`, `boolean`, `object`, `array`, `number`, `string`, `integer`)
- `format` (`date-time`, `date`, `time`, `duration`, `email`, `hostname`, `uri`, `uri-reference`, `uuid`, `ipv4`,
`ipv6`)
- `enum`
- `const`
- `multipleOf`
- `maximum`
- `exclusiveMaximum`
- `minimum`
- `exclusiveMinimum`
- `minItems`
- `maxItems`
- `maxLength`
- `minLength`
- `pattern`
- `required`

## Not Currently Implemented

- `$recursiveRef`
- `$recursiveAnchor`
- `$anchor`
- `$vocabulary`
- `patternProperties`
- `additionalProperties`
- `unevaluatedProperties`
- `additionalItems`
- `unevaluatedItems`
- `dependentSchemas`
- `dependentRequired`
- `contains`
- `uniqueItems`
- `maxContains`
- `minContains`
- `contentEncoding`
- `contentMediaType`
- `contentSchema`
- `deprecated`
- `readonly`
- `writeOnly`
- `examples`
- `format` (`idn-email`, `idn-hostname`, `ipv4`, `ipv6`, `url`, `url-reference`, `iri`, `iri-reference`, `url-template`,
`json-pointer`, `relative-json-pointer`)

More documentation to follow.

## Dependency Specification

The latest version of the library is 0.6, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.json</groupId>
      <artifactId>json-kotlin-schema</artifactId>
      <version>0.6</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.json:json-kotlin-schema:0.6'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.json:json-kotlin-schema:0.6")
```

Peter Wall

2020-08-23
