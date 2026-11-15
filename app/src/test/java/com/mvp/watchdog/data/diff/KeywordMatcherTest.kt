package com.mvp.watchdog.data.diff

import org.junit.Assert.assertEquals
import org.junit.Test

class KeywordMatcherTest {

    @Test
    fun singleMatch() {
        assertEquals(listOf("招聘"), KeywordMatcher.matches("招聘", "看到招聘公告"))
    }

    @Test
    fun splitsOnPunct() {
        assertEquals(listOf("护理岗"), KeywordMatcher.matches("护理岗, 教师", "护理岗在招"))
    }

    @Test
    fun caseInsensitive() {
        assertEquals(listOf("ABC"), KeywordMatcher.matches("ABC", "abc"))
    }

    @Test
    fun emptyReturnsEmpty() {
        assertEquals(emptyList<String>(), KeywordMatcher.matches("", "x"))
    }

    @Test
    fun noMatchReturnsEmpty() {
        assertEquals(emptyList<String>(), KeywordMatcher.matches("招聘", "无"))
    }
}
