package com.mvp.watchdog.work

import com.mvp.watchdog.data.db.Monitor

object CheckSchedule {

    /** 每个监控按自己的频率到期才检查；最小 15 分钟（WorkManager 周期下限）。 */
    fun isDue(monitor: Monitor, now: Long): Boolean {
        if (!monitor.enabled) return false
        if (monitor.lastCheckedAt <= 0L) return true
        val interval = monitor.intervalMinutes.coerceAtLeast(15) * 60_000L
        return now - monitor.lastCheckedAt >= interval
    }
}
