package com.mvp.watchdog.ui.monitordetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.diff.TextDiffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DiffUi(
    val title: String,
    val text: String,
    val added: List<String>,
    val removed: List<String>
)

class DiffViewModel(
    private val dataSource: MonitorDataSource,
    private val monitorId: Long
) : ViewModel() {

    private val _ui = MutableStateFlow<DiffUi?>(null)
    val ui: StateFlow<DiffUi?> = _ui

    init { load() }

    fun load() = viewModelScope.launch {
        val snaps = dataSource.getSnapshots(monitorId).first()
        val latest = snaps.firstOrNull()
        _ui.value = when {
            latest == null -> DiffUi("（无快照）", "", emptyList(), emptyList())
            snaps.size >= 2 -> {
                val diff = TextDiffer.diff(snaps[1].textContent, latest.textContent)
                DiffUi(latest.title, latest.textContent, diff.added, diff.removed)
            }
            else -> DiffUi(latest.title, latest.textContent, emptyList(), emptyList())
        }
    }

    companion object {
        fun factory(dataSource: MonitorDataSource, monitorId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { DiffViewModel(dataSource, monitorId) }
        }
    }
}
