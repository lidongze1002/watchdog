package com.mvp.watchdog.ui.monitorlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.db.Monitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitorListViewModel(private val dataSource: MonitorDataSource) : ViewModel() {

    val monitors: StateFlow<List<Monitor>> = dataSource.listMonitors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(id: Long) = viewModelScope.launch { dataSource.deleteMonitor(id) }

    companion object {
        fun factory(dataSource: MonitorDataSource): ViewModelProvider.Factory = viewModelFactory {
            initializer { MonitorListViewModel(dataSource) }
        }
    }
}
