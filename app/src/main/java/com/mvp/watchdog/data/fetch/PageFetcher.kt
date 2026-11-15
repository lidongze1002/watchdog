package com.mvp.watchdog.data.fetch

import okhttp3.OkHttpClient
import okhttp3.Request

data class FetchedPage(
    val html: String,
    val ok: Boolean,
    val errorMessage: String? = null
)

class PageFetcher(private val client: OkHttpClient = OkHttpClient()) {

    fun fetch(url: String): FetchedPage {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (compatible; Watchdog/0.1)")
                .build()
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    FetchedPage("", false, "HTTP ${resp.code}")
                } else {
                    FetchedPage(resp.body?.string() ?: "", true)
                }
            }
        } catch (e: Exception) {
            FetchedPage("", false, e.message ?: "网络错误")
        }
    }
}
