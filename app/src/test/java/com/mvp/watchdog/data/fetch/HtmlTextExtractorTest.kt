package com.mvp.watchdog.data.fetch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlTextExtractorTest {

    @Test
    fun readsTitle() {
        assertEquals("招聘", HtmlTextExtractor.extract("<title>招聘</title><p>正文</p>").title)
    }

    @Test
    fun stripsScriptStyle() {
        val text = HtmlTextExtractor.extract("<script>alert(1)</script><style>.a{color:red}</style><p>正文</p>").text
        assertFalse(text.contains("alert"))
        assertFalse(text.contains("color:red"))
    }

    @Test
    fun collectsBodyText() {
        val text = HtmlTextExtractor.extract("<p>正文</p><p>第二段</p>").text
        assertTrue(text.contains("正文"))
        assertTrue(text.contains("第二段"))
        assertFalse(text.contains("<p>"))
    }

    @Test
    fun emptyOnNoContent() {
        assertTrue(HtmlTextExtractor.extract("<html></html>").empty)
    }
}
