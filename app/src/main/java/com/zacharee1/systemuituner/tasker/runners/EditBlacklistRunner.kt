package com.zacharee1.systemuituner.tasker.runners

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.tasker.inputs.BlacklistInput
import com.zacharee1.systemuituner.util.changeBlacklist

class EditBlacklistRunner : TaskerPluginRunnerActionNoOutput<BlacklistInput>() {
    override fun run(context: Context, input: TaskerInput<BlacklistInput>): TaskerPluginResult<Unit> {
        val key = input.regular.key ?: return TaskerPluginResultError(IllegalStateException("Parameter 'key' must be set"))
        val remove = input.regular.remove

        return if (context.changeBlacklist(key, remove)) TaskerPluginResultSucess()
        else TaskerPluginResultError(SecurityException("Permission denied or incorrect parameters"))
    }
}