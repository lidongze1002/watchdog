package com.mvp.watchdog.data.fetch

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class PageFetcherErrorTest {

    @Test
    fun unknownHostIsFriendly() {
        assertEquals("域名无法解析，请检查网址或网络", PageFetcher.friendlyError(UnknownHostException("x")))
    }

    @Test
    fun timeoutIsFriendly() {
        assertEquals("连接超时，稍后再试", PageFetcher.friendlyError(SocketTimeoutException()))
    }

    @Test
    fun refusedConnectionIsFriendly() {
        assertEquals(
            "无法连接服务器（网址失效或被拒绝）",
            PageFetcher.friendlyError(ConnectException("refused"))
        )
    }

    @Test
    fun sslErrorIsFriendly() {
        assertEquals("HTTPS 证书错误，暂时无法抓取", PageFetcher.friendlyError(SSLHandshakeException("bad")))
    }

    @Test
    fun fallsBackToMessage() {
        assertEquals("boom", PageFetcher.friendlyError(IOException("boom")))
        assertEquals("网络错误", PageFetcher.friendlyError(IOException()))
    }
}
