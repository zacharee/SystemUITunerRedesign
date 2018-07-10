package com.zacharee1.systemuituner.tasker.activities

import android.preference.EditTextPreference
import android.preference.Preference
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.tasker.inputs.SettingsInput
import com.zacharee1.systemuituner.tasker.runners.EditSettingsRunner

abstract class BaseSettingsEditActivity : BaseEditActivity<SettingsInput>() {
    companion object {
        const val KEY = "key"
        const val VALUE = "value"
    }

    override val inputForTasker: TaskerInput<SettingsInput>
        get() = TaskerInput(SettingsInput(key, value, type))

    override val helper: TaskerPluginConfigHelperNoOutput<SettingsInput, EditSettingsRunner>
            by lazy {
                object : TaskerPluginConfigHelperNoOutput<SettingsInput, EditSettingsRunner>(this) {
                    override val runnerClass = EditSettingsRunner::class.java
                    override val inputClass = SettingsInput::class.java
                }
            }

    override val prefsRes: Int = R.xml.pref_edit_settings

    internal abstract val type: String?

    internal var key: String? = null
    internal var value: String? = null

    override fun assignFromInput(input: TaskerInput<SettingsInput>) {
        key = input.regular.key
        value = input.regular.value
    }

    override fun onFinishFragmentAttach() {
        val keyPref = fragment.findPreference(KEY) as EditTextPreference
        val valPref = fragment.findPreference(VALUE) as EditTextPreference

        keyPref.summary = key
        valPref.summary = value

        keyPref.text = key
        valPref.text = value

        val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue.toString()

            when (preference.key) {
                KEY -> key = newValue.toString()
                VALUE -> value = newValue.toString()
            }

            true
        }

        keyPref.onPreferenceChangeListener = listener
        valPref.onPreferenceChangeListener = listener
    }
}