package com.hamurcuabi.layoutbounder.adb

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val ADB_DISPATCHER: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
