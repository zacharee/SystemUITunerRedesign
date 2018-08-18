package com.zacharee1.systemuituner.tasker.runners

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.tasker.inputs.SettingsInput
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem

class EditSettingsRunner: TaskerPluginRunnerActionNoOutput<SettingsInput>() {
    companion object {
        const val GLOBAL = "Settings\$Global"
        const val SECURE = "Settings\$Secure"
        const val SYSTEM = "Settings\$System"
    }

    override fun run(context: Context, input: TaskerInput<SettingsInput>): TaskerPluginResult<Unit> {
        val actualInput = input.regular
        val key = actualInput.key
        val value = actualInput.value
        val type = actualInput.type

        if (key == null) return TaskerPluginResultError(IllegalArgumentException("Parameter 'key' must not be null"))

        return if (write(context, key, value, type)) TaskerPluginResultSucess()
        else TaskerPluginResultError(SecurityException("Permission denied or incorrect parameter(s)"))
    }

    private fun write(context: Context, key: String, value: String?, type: String?): Boolean {
        return when (type) {
            GLOBAL -> context.writeGlobal(key, value)
            SECURE -> context.writeSecure(key, value)
            SYSTEM -> context.writeSystem(key, value)
            else -> false
        }
    }
}