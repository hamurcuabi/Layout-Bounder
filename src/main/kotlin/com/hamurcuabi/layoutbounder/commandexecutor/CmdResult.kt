package com.hamurcuabi.layoutbounder.commandexecutor

data class CmdResult(val exitCode: Int, val output: String) {
    val isError: Boolean = exitCode != 0
}
