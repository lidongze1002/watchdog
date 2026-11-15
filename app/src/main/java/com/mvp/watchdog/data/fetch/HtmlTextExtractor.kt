package com.mvp.watchdog.data.fetch

import org.jsoup.Jsoup

data class ExtractedPage(
    val title: String,
    val text: String,
    val empty: Boolean
)

object HtmlTextExtractor {
    fun extract(html: String): ExtractedPage {
        val doc = Jsoup.parse(html)
        doc.select("script,style,noscript,nav,header,footer").remove()
        val title = doc.title().trim()
        val body = doc.body()
        val text = if (body != null) {
            (body.select("article, main").firstOrNull() ?: body).text()
        } else {
            doc.text()
        }
        return ExtractedPage(
            title = title,
            text = text.trim(),
            empty = text.isBlank()
        )
    }
}
