package com.mvp.watchdog.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(ms: Long): String =
    if (ms == 0L) "尚未检查" else SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(ms))
