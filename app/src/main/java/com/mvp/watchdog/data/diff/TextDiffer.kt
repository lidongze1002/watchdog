package com.mvp.watchdog.data.diff

data class DiffResult(
    val added: List<String>,
    val removed: List<String>
) {
    val changed: Boolean get() = added.isNotEmpty() || removed.isNotEmpty()
}

object TextDiffer {
    fun diff(oldText: String, newText: String): DiffResult {
        val old = lines(oldText)
        val new = lines(newText)
        val removed = old - new.toSet()
        val added = new - old.toSet()
        return DiffResult(added = added, removed = removed)
    }

    private fun lines(text: String): List<String> =
        text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
}
