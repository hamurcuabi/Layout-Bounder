package com.hamurcuabi.layoutbounder.model

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.util.*

class CommandHistory {

    private val logEntries = LinkedList<LogEntry>()

    var listener: Listener? = null
        set(value) {
            field = value
            notifyListener()
        }

    suspend fun add(entry: LogEntry) = withContext(Main) {
        logEntries.add(entry)
        ensureCapacity()
        notifyListener()
    }

    fun clear() {
        logEntries.clear()
        notifyListener()
    }

    private fun ensureCapacity() {
        if (logEntries.size > CAPACITY) {
            repeat(logEntries.size - CAPACITY) {
                logEntries.removeFirst()
            }
        }
    }

    private fun notifyListener() {
        listener?.onLogEntriesModified(logEntries)
    }

    interface Listener {
        fun onLogEntriesModified(entries: List<LogEntry>)
    }

    private companion object {
        private const val CAPACITY = 100
    }
}
