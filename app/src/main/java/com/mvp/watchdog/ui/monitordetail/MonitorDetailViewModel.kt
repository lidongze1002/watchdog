package com.mvp.watchdog.ui.monitordetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mvp.watchdog.data.CheckPipeline
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.db.ChangeEvent
import com.mvp.watchdog.data.db.Monitor
import com.mvp.watchdog.data.db.Snapshot
import com.mvp.watchdog.notify.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitorDetailViewModel(
    application: Application,
    private val dataSource: MonitorDataSource,
    private val monitorId: Long
) : AndroidViewModel(application) {

    private val _monitor = MutableStateFlow<Monitor?>(null)
    val monitor: StateFlow<Monitor?> = _monitor

    private val _checking = MutableStateFlow(false)
    val checking: StateFlow<Boolean> = _checking

    private val _checkMessage = MutableStateFlow<String?>(null)
    val checkMessage: StateFlow<String?> = _checkMessage

    val events: StateFlow<List<ChangeEvent>> = dataSource.getChangeEvents(monitorId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestSnapshot: StateFlow<Snapshot?> = dataSource.getSnapshots(monitorId)
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            _monitor.value = dataSource.getMonitor(monitorId)
            dataSource.markEventsSeen(monitorId)
        }
    }

    fun check() = viewModelScope.launch {
        _checking.value = true
        _checkMessage.value = null
        val outcome = CheckPipeline(dataSource).check(monitorId)
        _monitor.value = dataSource.getMonitor(monitorId)
        dataSource.markEventsSeen(monitorId)
        if (outcome.notify) {
            NotificationHelper.notify(
                getApplication(),
                _monitor.value?.name ?: "监控",
                "检测到变化：" + outcome.matchedKeywords.joinToString(",")
            )
        }
        _checkMessage.value = when {
            outcome.errorMessage != null -> outcome.errorMessage
            outcome.changed -> "检测到变化" +
                if (outcome.matchedKeywords.isNotEmpty()) "：" + outcome.matchedKeywords.joinToString(",") else ""
            else -> "无变化"
        }
        _checking.value = false
    }

    companion object {
        fun factory(app: Application, dataSource: MonitorDataSource, monitorId: Long): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { MonitorDetailViewModel(app, dataSource, monitorId) }
            }
    }
}
