package com.zacharee1.systemuituner.tasker.inputs

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot

@TaskerInputRoot
class BlacklistInput @JvmOverloads constructor (
        @field:TaskerInputField("key") var key: String? = null,
        @field:TaskerInputField("remove") var remove: Boolean = false)