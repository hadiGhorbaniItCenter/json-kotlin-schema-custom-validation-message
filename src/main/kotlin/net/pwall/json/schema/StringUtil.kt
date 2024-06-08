package net.pwall.json.schema

fun persianNumberToEnglishNumber(persian: String): String {
    var persianStr = persian
    val numbersMap = mapOf(
        '۱' to '1',
        '۲' to '2',
        '۳' to '3',
        '۴' to '4',
        '۵' to '5',
        '۶' to '6',
        '۷' to '7',
        '۸' to '8',
        '۹' to '9',
        '۰' to '0'
    )

    persian.forEach { oldChar ->
        numbersMap[oldChar]?.let { newChar ->
            persianStr = persianStr.replace(oldChar, newChar)
        }
    }
    return persianStr
}