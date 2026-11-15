package com.mvp.watchdog.data.diff

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TextDifferTest {

    @Test
    fun identicalIsUnchanged() {
        val r = TextDiffer.diff("a\nb", "a\nb")
        assertFalse(r.changed)
    }

    @Test
    fun detectsAddedLine() {
        val r = TextDiffer.diff("a", "a\nb")
        assertEquals(listOf("b"), r.added)
    }

    @Test
    fun detectsRemovedLine() {
        val r = TextDiffer.diff("a\nb", "a")
        assertEquals(listOf("b"), r.removed)
    }

    @Test
    fun detectsBoth() {
        val r = TextDiffer.diff("a\nb", "a\nc")
        assertEquals(listOf("c"), r.added)
        assertEquals(listOf("b"), r.removed)
    }
}
