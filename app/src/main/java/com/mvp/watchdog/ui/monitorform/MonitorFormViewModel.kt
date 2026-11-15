package com.mvp.watchdog.ui.monitorform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mvp.watchdog.data.CheckPipeline
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.db.Monitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MonitorFormState(
    val url: String = "",
    val name: String = "",
    val keywords: String = "",
    val saving: Boolean = false,
    val error: String? = null
)

class MonitorFormViewModel(private val dataSource: MonitorDataSource) : ViewModel() {

    private val _state = MutableStateFlow(MonitorFormState())
    val state: StateFlow<MonitorFormState> = _state

    fun onUrlChange(v: String) { _state.value = _state.value.copy(url = v, error = null) }
    fun onNameChange(v: String) { _state.value = _state.value.copy(name = v) }
    fun onKeywordsChange(v: String) { _state.value = _state.value.copy(keywords = v) }

    fun save(onSaved: () -> Unit) {
        val s = _state.value
        val url = s.url.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            _state.value = s.copy(error = "URL 需以 http:// 或 https:// 开头")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, error = null)
            val id = dataSource.saveMonitor(
                Monitor(
                    url = url,
                    name = s.name.ifBlank { url },
                    keywords = s.keywords.trim()
                )
            )
            CheckPipeline(dataSource).check(id)
            _state.value = MonitorFormState()
            onSaved()
        }
    }

    companion object {
        fun factory(dataSource: MonitorDataSource): ViewModelProvider.Factory = viewModelFactory {
            initializer { MonitorFormViewModel(dataSource) }
        }
    }
}
