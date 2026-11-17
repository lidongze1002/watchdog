package com.mvp.watchdog.work

import com.mvp.watchdog.data.db.Monitor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckScheduleTest {

    private val now = 1_800_000_000_000L

    private fun monitor(
        lastCheckedAt: Long,
        intervalMinutes: Int = 60,
        enabled: Boolean = true
    ) = Monitor(
        id = 1,
        url = "https://example.com",
        name = "示例",
        keywords = "",
        enabled = enabled,
        lastCheckedAt = lastCheckedAt,
        intervalMinutes = intervalMinutes
    )

    @Test
    fun neverCheckedIsDue() {
        assertTrue(CheckSchedule.isDue(monitor(0), now))
    }

    @Test
    fun notDueBeforeInterval() {
        assertFalse(CheckSchedule.isDue(monitor(now - 59 * 60_000L, 60), now))
    }

    @Test
    fun dueAtExactInterval() {
        assertTrue(CheckSchedule.isDue(monitor(now - 60 * 60_000L, 60), now))
    }

    @Test
    fun honorsLongerIntervals() {
        assertFalse(CheckSchedule.isDue(monitor(now - 2 * 3600_000L, 360), now))
        assertTrue(CheckSchedule.isDue(monitor(now - 7 * 3600_000L, 360), now))
    }

    @Test
    fun clampsToFifteenMinutes() {
        assertFalse(CheckSchedule.isDue(monitor(now - 5 * 60_000L, 1), now))
        assertTrue(CheckSchedule.isDue(monitor(now - 15 * 60_000L, 1), now))
    }

    @Test
    fun disabledIsNeverDue() {
        assertFalse(CheckSchedule.isDue(monitor(0, enabled = false), now))
    }
}
