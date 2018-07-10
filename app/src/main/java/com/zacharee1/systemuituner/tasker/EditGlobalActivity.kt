package com.zacharee1.systemuituner.tasker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.support.v4.content.LocalBroadcastManager
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.util.SettingsUtils
import java.io.Serializable

@SuppressLint("Registered")
abstract class BaseEditActivity : BaseAnimActivity(), TaskerPluginConfig<Input> {
    companion object {
        const val KEY = "key"
        const val VALUE = "value"
        const val TYPE = "type"

        const val UPDATE = "update"
    }

    internal abstract val type: String?

    override val context: Context
        get() = this

    override val inputForTasker: TaskerInput<Input>
        get() = TaskerInput(Input(key, value, type))

    lateinit var helper: TaskerPluginConfigHelperNoOutput<Input, EditBase>

    internal var key: String? = null
    internal var value: String? = null

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == UPDATE) {
                when (intent.getStringExtra(TYPE)) {
                    KEY -> key = intent.getStringExtra(KEY)
                    VALUE -> value = intent.getStringExtra(VALUE)
                }
            }
        }
    }
    private val fragment = ConfigFragment()

    override fun assignFromInput(input: TaskerInput<Input>) {
        key = input.regular.key
        value = input.regular.value

        fragment.setText(key, value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        helper = object : TaskerPluginConfigHelperNoOutput<Input, EditBase>(this) {
            override val runnerClass = EditBase::class.java
            override val inputClass = Input::class.java
        }

        helper.onCreate()

        setContentView(R.layout.activity_item_list)

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, IntentFilter(UPDATE))

        fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_main, fragment, "tasker")
                ?.commit()
    }

    override fun onBackPressed() {
        helper.finishForTasker()
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
    }

    class ConfigFragment : AnimFragment() {
        var preInitKey: String? = null
        var preInitValue: String? = null

        override fun onAnimationFinishedEnter(enter: Boolean) {
            addPreferencesFromResource(R.xml.pref_tasker_plugin)

            val keyPref = findPreference(KEY) as EditTextPreference
            val valPref = findPreference(VALUE) as EditTextPreference

            keyPref.summary = preInitKey
            valPref.summary = preInitValue

            keyPref.text = preInitKey
            valPref.text = preInitValue

            val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue.toString()

                val update = Intent(UPDATE)
                update.putExtra(TYPE, preference.key)

                when (preference.key) {
                    KEY -> update.putExtra(KEY, newValue.toString())
                    VALUE -> update.putExtra(VALUE, newValue.toString())
                }

                LocalBroadcastManager.getInstance(activity).sendBroadcast(update)

                true
            }

            keyPref.onPreferenceChangeListener = listener
            valPref.onPreferenceChangeListener = listener
        }

        fun setText(key: String?, value: String?) {
            preInitKey = key
            preInitValue = value
        }
    }
}

class EditGlobalActivity(override val type: String? = Type.GLOBAL) : BaseEditActivity()
class EditSecureActivity(override val type: String? = Type.SECURE) : BaseEditActivity()
class EditSystemActivity(override val type: String? = Type.SYSTEM) : BaseEditActivity()

class EditBase: TaskerPluginRunnerActionNoOutput<Input>() {
    override fun run(context: Context, input: TaskerInput<Input>): TaskerPluginResult<Unit> {
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
            Type.GLOBAL -> SettingsUtils.writeGlobal(context, key, value)
            Type.SECURE -> SettingsUtils.writeSecure(context, key, value)
            Type.SYSTEM -> SettingsUtils.writeSystem(context, key, value)
            else -> false
        }
    }
}

@TaskerInputRoot
class Input @JvmOverloads constructor (
        @field:TaskerInputField("key") var key: String? = null,
        @field:TaskerInputField("value") var value: String? = null,
        @field:TaskerInputField("type") var type: String? = null)

object Type : Serializable {
    const val GLOBAL = "Settings\$Global"
    const val SECURE = "Settings\$Secure"
    const val SYSTEM = "Settings\$System"
}