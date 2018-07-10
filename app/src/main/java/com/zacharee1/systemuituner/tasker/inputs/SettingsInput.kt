package com.zacharee1.systemuituner.tasker.inputs

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot

@TaskerInputRoot
class SettingsInput @JvmOverloads constructor (
        @field:TaskerInputField("key") var key: String? = null,
        @field:TaskerInputField("value") var value: String? = null,
        @field:TaskerInputField("type") var type: String? = null)