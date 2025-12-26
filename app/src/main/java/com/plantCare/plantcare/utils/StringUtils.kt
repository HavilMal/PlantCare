package com.plantCare.plantcare.utils

fun listToStringWith(list: List<String>, lastSeparator: String, separator: String = ", "): String {
    list.ifEmpty {
        return ""
    }

    if (list.size == 1) {
        return list.first()
    }

    val body = list.dropLast(1).joinToString(separator = separator)
    val last = list.last()

    return "$body $lastSeparator $last"
}

fun String.ifNotEmpty(fn: (String) -> Unit) {
    if (this.isNotEmpty()) {
        fn(this)
    }
}