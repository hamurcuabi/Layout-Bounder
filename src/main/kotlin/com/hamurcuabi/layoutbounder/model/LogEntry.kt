package com.hamurcuabi.layoutbounder.model

sealed class LogEntry(val text: String) {
    class Command(args: String) : LogEntry("adb $args")
    class Output(text: String) : LogEntry(text)
}
