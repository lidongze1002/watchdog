package com.mvp.watchdog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mvp.watchdog.ui.monitorform.MonitorFormScreen
import com.mvp.watchdog.ui.monitorform.MonitorFormViewModel
import com.mvp.watchdog.ui.monitorlist.MonitorListScreen
import com.mvp.watchdog.ui.monitorlist.MonitorListViewModel
import com.mvp.watchdog.ui.monitordetail.DiffScreen
import com.mvp.watchdog.ui.monitordetail.DiffViewModel
import com.mvp.watchdog.ui.monitordetail.MonitorDetailScreen
import com.mvp.watchdog.ui.monitordetail.MonitorDetailViewModel
import com.mvp.watchdog.ui.theme.WatchdogTheme

sealed interface Screen {
    data object List : Screen
    data object Form : Screen
    data class Detail(val id: Long) : Screen
    data class Diff(val id: Long) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WatchdogTheme {
                val container = (application as WatchdogApp).container
                var stack by remember { mutableStateOf(listOf<Screen>(Screen.List)) }
                val current = stack.last()

                val ctx = LocalContext.current
                val permLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                BackHandler { if (stack.size > 1) stack = stack.dropLast(1) }

                when (current) {
                    Screen.List -> MonitorListScreen(
                        vm = viewModel(factory = MonitorListViewModel.factory(container.monitorDataSource)),
                        onAdd = { stack = stack + Screen.Form },
                        onOpen = { id -> stack = stack + Screen.Detail(id) }
                    )
                    Screen.Form -> MonitorFormScreen(
                        vm = viewModel(factory = MonitorFormViewModel.factory(container.monitorDataSource)),
                        onBack = { stack = stack.dropLast(1) }
                    )
                    is Screen.Detail -> MonitorDetailScreen(
                        vm = viewModel(
                            key = "detail_${current.id}",
                            factory = MonitorDetailViewModel.factory(application, container.monitorDataSource, current.id)
                        ),
                        onBack = { stack = stack.dropLast(1) },
                        onDiff = { id -> stack = stack + Screen.Diff(id) }
                    )
                    is Screen.Diff -> DiffScreen(
                        vm = viewModel(
                            key = "diff_${current.id}",
                            factory = DiffViewModel.factory(container.monitorDataSource, current.id)
                        ),
                        onBack = { stack = stack.dropLast(1) }
                    )
                }
            }
        }
    }
}
