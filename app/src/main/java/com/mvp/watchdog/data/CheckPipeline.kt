package com.mvp.watchdog.data

import com.mvp.watchdog.data.db.ChangeEvent
import com.mvp.watchdog.data.db.Snapshot
import com.mvp.watchdog.data.diff.KeywordMatcher
import com.mvp.watchdog.data.diff.TextDiffer
import com.mvp.watchdog.data.fetch.HtmlTextExtractor
import com.mvp.watchdog.data.fetch.PageFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CheckOutcome(
    val changed: Boolean,
    val matchedKeywords: List<String>,
    val errorMessage: String?
) {
    val notify: Boolean get() = changed && matchedKeywords.isNotEmpty()
}

class CheckPipeline(
    private val dataSource: MonitorDataSource,
    private val fetcher: PageFetcher = PageFetcher()
) {

    suspend fun check(monitorId: Long): CheckOutcome {
        val monitor = dataSource.getMonitor(monitorId)
            ?: return CheckOutcome(false, emptyList(), "监控不存在")

        val fetched = withContext(Dispatchers.IO) { fetcher.fetch(monitor.url) }
        if (!fetched.ok) {
            val message = fetched.errorMessage ?: "抓取失败"
            val now = System.currentTimeMillis()
            dataSource.saveMonitor(
                monitor.copy(lastCheckedAt = now, lastError = message, lastErrorAt = now)
            )
            return CheckOutcome(false, emptyList(), message)
        }

        val page = HtmlTextExtractor.extract(fetched.html)
        if (page.empty) {
            val message = "页面无可提取正文（可能为 JS 渲染或空页）"
            val now = System.currentTimeMillis()
            dataSource.saveMonitor(
                monitor.copy(lastCheckedAt = now, lastError = message, lastErrorAt = now)
            )
            return CheckOutcome(false, emptyList(), message)
        }

        val hash = page.text.hashCode().toString()
        val latest = dataSource.latestSnapshot(monitorId)
        val now = System.currentTimeMillis()

        if (latest != null && latest.textHash == hash) {
            dataSource.saveMonitor(
                monitor.copy(lastCheckedAt = now, lastError = "", lastErrorAt = 0)
            )
            return CheckOutcome(false, emptyList(), null)
        }

        val changed = latest != null
        val matched = if (monitor.keywords.isBlank()) {
            listOf("（页面变化）")
        } else {
            KeywordMatcher.matches(monitor.keywords, page.text)
        }

        val snapshotId = dataSource.saveSnapshot(
            Snapshot(
                monitorId = monitorId,
                fetchedAt = now,
                title = page.title,
                textHash = hash,
                textContent = page.text,
                changed = changed
            )
        )

        if (changed) {
            val added = latest?.let { TextDiffer.diff(it.textContent, page.text).added } ?: emptyList()
            dataSource.saveChangeEvent(
                ChangeEvent(
                    monitorId = monitorId,
                    snapshotId = snapshotId,
                    occurredAt = now,
                    matchedKeywords = matched.joinToString(","),
                    snippet = added.joinToString(" ").take(200)
                )
            )
        }

        dataSource.saveMonitor(
            monitor.copy(
                lastCheckedAt = now,
                lastSnapshotId = snapshotId,
                lastError = "",
                lastErrorAt = 0
            )
        )
        return CheckOutcome(changed, matched, null)
    }
}
