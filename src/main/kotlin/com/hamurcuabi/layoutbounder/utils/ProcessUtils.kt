package com.hamurcuabi.layoutbounder.utils

fun Process.output(): String = this.inputStream?.bufferedReader()?.readText()?.trim('\n').orEmpty()
