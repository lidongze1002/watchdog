package com.mvp.watchdog.data.diff

object KeywordMatcher {
    fun matches(keywords: String, text: String): List<String> {
        val keys = keywords.split(Regex("[，,。.;；、\\s]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val lower = text.lowercase()
        return keys.filter { lower.contains(it.lowercase()) }
    }
}
