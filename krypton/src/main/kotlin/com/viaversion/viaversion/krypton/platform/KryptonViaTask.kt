package com.viaversion.viaversion.krypton.platform

import com.viaversion.viaversion.api.platform.PlatformTask
import org.kryptonmc.api.scheduling.Task

class KryptonViaTask(private val task: Task) : PlatformTask<Task> {

    override fun getObject(): Task = task

    override fun cancel() {
        task.cancel()
    }
}
