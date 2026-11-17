package com.mvp.watchdog.data.fetch

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

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
                .header("User-Agent", "Mozilla/5.0 (compatible; Watchdog/0.2)")
                .build()
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    FetchedPage("", false, "网站返回 HTTP ${resp.code}")
                } else {
                    FetchedPage(resp.body?.string() ?: "", true)
                }
            }
        } catch (e: Exception) {
            FetchedPage("", false, friendlyError(e))
        }
    }

    companion object {
        /** 把技术异常翻成人话，用户看得懂才知道怎么改。 */
        fun friendlyError(e: Throwable): String = when (e) {
            is UnknownHostException -> "域名无法解析，请检查网址或网络"
            is SocketTimeoutException -> "连接超时，稍后再试"
            is ConnectException -> "无法连接服务器（网址失效或被拒绝）"
            is SSLException -> "HTTPS 证书错误，暂时无法抓取"
            else -> e.message?.takeIf { it.isNotBlank() } ?: "网络错误"
        }
    }
}
