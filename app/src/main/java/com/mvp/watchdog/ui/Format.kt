package com.mvp.watchdog.ui

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

fun formatTime(ms: Long): String =
    if (ms == 0L) "尚未检查" else SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(ms))

fun formatClock(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))

fun intervalLabel(minutes: Int): String = when (minutes) {
    15 -> "每 15 分钟"
    60 -> "每 1 小时"
    360 -> "每 6 小时"
    1440 -> "每天"
    else -> "每 $minutes 分钟"
}

/** 列表里按“今天/昨天/M月d日”分组 */
fun dayLabel(ms: Long, today: LocalDate = LocalDate.now()): String {
    if (ms == 0L) return "更早"
    val date = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
    return when {
        date == today -> "今天"
        date == today.minusDays(1) -> "昨天"
        date.year == today.year -> "${date.monthValue}月${date.dayOfMonth}日"
        else -> "${date.year}年${date.monthValue}月${date.dayOfMonth}日"
    }
}
