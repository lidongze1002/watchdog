package com.mvp.watchdog.ui.allchanges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.db.ChangeEventRow
import com.mvp.watchdog.ui.dayLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ChangeGroup(val label: String, val rows: List<ChangeEventRow>)

class AllChangesViewModel(private val dataSource: MonitorDataSource) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val groups: StateFlow<List<ChangeGroup>> = combine(
        dataSource.observeAllChangeEvents(),
        _query
    ) { rows, query ->
        val filtered = if (query.isBlank()) {
            rows
        } else {
            rows.filter {
                it.monitorName.contains(query, ignoreCase = true) ||
                    it.snippet.contains(query, ignoreCase = true) ||
                    it.matchedKeywords.contains(query, ignoreCase = true)
            }
        }
        filtered.groupBy { dayLabel(it.occurredAt) }
            .map { (label, list) -> ChangeGroup(label, list) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: StateFlow<Int> = combine(dataSource.observeAllChangeEvents(), _query) { rows, query ->
        if (query.isBlank()) rows.size else rows.count {
            it.monitorName.contains(query, ignoreCase = true) ||
                it.snippet.contains(query, ignoreCase = true) ||
                it.matchedKeywords.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    companion object {
        fun factory(dataSource: MonitorDataSource): ViewModelProvider.Factory = viewModelFactory {
            initializer { AllChangesViewModel(dataSource) }
        }
    }
}
