package com.emrekalkan.bitcointicker.utils.ext

import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.format(this)
    } catch (ex: Exception) {
        ""
    }
}