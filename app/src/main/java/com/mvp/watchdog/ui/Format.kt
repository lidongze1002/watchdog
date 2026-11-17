package com.mvp.watchdog.ui

import com.mvp.watchdog.ui.i18n.Lang
import com.mvp.watchdog.ui.i18n.T
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private val dayMonthEn = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
private val fullDateEn = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

fun formatTime(ms: Long): String =
    if (ms == 0L) T("尚未检查", "Not checked yet")
    else SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(ms))

fun formatClock(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))

fun intervalLabel(minutes: Int): String = when (minutes) {
    15 -> T("每 15 分钟", "Every 15 min")
    60 -> T("每 1 小时", "Hourly")
    360 -> T("每 6 小时", "Every 6 hours")
    1440 -> T("每天", "Daily")
    else -> T("每 $minutes 分钟", "Every $minutes min")
}

/** 列表里按“今天/昨天/M月d日”分组 */
fun dayLabel(ms: Long, today: LocalDate = LocalDate.now()): String {
    if (ms == 0L) return T("更早", "Earlier")
    val date = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
    if (Lang.current == "en") {
        return when {
            date == today -> "Today"
            date == today.minusDays(1) -> "Yesterday"
            date.year == today.year -> date.format(dayMonthEn)
            else -> date.format(fullDateEn)
        }
    }
    return when {
        date == today -> "今天"
        date == today.minusDays(1) -> "昨天"
        date.year == today.year -> "${date.monthValue}月${date.dayOfMonth}日"
        else -> "${date.year}年${date.monthValue}月${date.dayOfMonth}日"
    }
}
