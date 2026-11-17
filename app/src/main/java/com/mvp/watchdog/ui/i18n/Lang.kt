package com.mvp.watchdog.ui.i18n

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * 应用内语言：中文 / English / 跟随系统。
 */
object Lang {
    private const val PREF = "watchdog_lang"
    private const val KEY = "lang"

    var preference: String by mutableStateOf("zh")
        private set
    var current: String by mutableStateOf("zh")
        private set

    fun load(context: Context) {
        val saved = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, "zh") ?: "zh"
        preference = saved
        current = effective(saved)
    }

    fun set(context: Context, code: String) {
        preference = code
        current = effective(code)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, code)
            .apply()
    }

    private fun effective(code: String): String = when (code) {
        "zh" -> "zh"
        "en" -> "en"
        else -> if (Locale.getDefault().language.startsWith("zh")) "zh" else "en"
    }
}

fun T(zh: String, en: String): String = if (Lang.current == "en") en else zh
