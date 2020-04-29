package org.kin.sdk.base.tools

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

data class ExecutorServices(
    val sequentialIO: ExecutorService = Executors.newSingleThreadExecutor(),
    val parallelIO: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
    val sequentialScheduled: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
)

fun submitOrRunOn(maybeExecutor: ExecutorService?, work: () -> Unit) =
    maybeExecutor?.submit(work) ?: work()
