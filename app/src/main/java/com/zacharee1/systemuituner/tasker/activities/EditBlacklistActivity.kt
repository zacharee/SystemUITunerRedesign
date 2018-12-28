package com.zacharee1.systemuituner.tasker.activities

import androidx.preference.EditTextPreference
import androidx.preference.SwitchPreference
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.tasker.inputs.BlacklistInput
import com.zacharee1.systemuituner.tasker.runners.EditBlacklistRunner

class EditBlacklistActivity : BaseEditActivity<BlacklistInput>() {
    companion object {
        const val KEY = "key"
        const val REMOVE = "remove"
    }

    private var key: String? = null
    private var remove: Boolean = false

    override val prefsRes: Int = R.xml.pref_edit_blacklist

    override val inputForTasker: TaskerInput<BlacklistInput>
        get() = TaskerInput(BlacklistInput(key, remove))

    override val helper: TaskerPluginConfigHelperNoOutput<BlacklistInput, EditBlacklistRunner>
            by lazy {
                object : TaskerPluginConfigHelperNoOutput<BlacklistInput, EditBlacklistRunner>(this) {
                    override val runnerClass = EditBlacklistRunner::class.java
                    override val inputClass = BlacklistInput::class.java
                }
            }

    override fun assignFromInput(input: TaskerInput<BlacklistInput>) {
        key = input.regular.key
        remove = input.regular.remove
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        val keyPref = fragment.findPreference(KEY) as EditTextPreference
        val remPref = fragment.findPreference(REMOVE) as SwitchPreference

        keyPref.summary = key
        keyPref.text = key

        remPref.isChecked = remove

        keyPref.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue.toString()
            key = newValue.toString()
            true
        }

        remPref.setOnPreferenceChangeListener { _, newValue ->
            remove = newValue.toString().toBoolean()
            true
        }
    }
}