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
open class BaseEditActivity : BaseAnimActivity(), TaskerPluginConfig<Input> {
    companion object {
        const val KEY = "key"
        const val VALUE = "value"
        const val TYPE = "type"

        const val UPDATE = "update"
    }

    override val context: Context
        get() = this

    override val inputForTasker: TaskerInput<Input>
        get() = TaskerInput(Input(key, value, type))

    var helper =
            object : TaskerPluginConfigHelperNoOutput<Input, EditBase>(this) {
                override val runnerClass = EditBase::class.java
                override val inputClass = Input::class.java
            }

    internal var key: String? = null
    internal var value: String? = null
    internal val type: Type?
        get() = when (this) {
            is EditGlobalActivity -> Type.GLOBAL
            is EditSecureActivity -> Type.SECURE
            is EditSystemActivity -> Type.SYSTEM
            else -> null
        }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == UPDATE) {
                key = intent.getStringExtra(KEY) ?: key
                value = intent.getStringExtra(VALUE) ?: value
            }
        }
    }

    override fun assignFromInput(input: TaskerInput<Input>) {
        key = input.regular.key
        value = input.regular.value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_list)

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, IntentFilter(UPDATE))

        val fragment = ConfigFragment()
        val args = Bundle()
        args.putString(KEY, key)
        args.putString(VALUE, value)
        args.putSerializable(TYPE, type)
        fragment.arguments = args

        fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_main, fragment, "tasker")
                ?.commit()
    }

    override fun onDestroy() {
        super.onDestroy()



        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
    }

    class ConfigFragment : AnimFragment() {
        override fun onAnimationFinishedEnter(enter: Boolean) {
            addPreferencesFromResource(R.xml.pref_tasker_plugin)

            val key = arguments.getString(KEY)
            val value = arguments.getString(VALUE)

            val keyPref = findPreference(KEY) as EditTextPreference
            val valPref = findPreference(VALUE) as EditTextPreference

            keyPref.summary = key
            valPref.summary = value

            keyPref.text = key
            valPref.text = value

            val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue.toString()

                val update = Intent(UPDATE)

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
    }
}

class EditGlobalActivity : BaseEditActivity()
class EditSecureActivity : BaseEditActivity()
class EditSystemActivity : BaseEditActivity()

abstract class EditBase: TaskerPluginRunnerActionNoOutput<Input>() {
    override fun run(context: Context, input: TaskerInput<Input>): TaskerPluginResult<Unit> {
        val actualInput = input.regular
        val key = actualInput.key
        val value = actualInput.value
        val type = actualInput.type

        if (key == null) return TaskerPluginResultError(IllegalArgumentException("Parameter 'key' must not be null"))

        return if (write(context, key, value, type)) TaskerPluginResultSucess()
        else TaskerPluginResultError(SecurityException("Permission denied or incorrect parameter(s)"))
    }

    private fun write(context: Context, key: String, value: String?, type: Type?): Boolean {
        return when (type) {
            Type.GLOBAL -> SettingsUtils.writeGlobal(context, key, value)
            Type.SECURE -> SettingsUtils.writeSecure(context, key, value)
            Type.SYSTEM -> SettingsUtils.writeSystem(context, key, value)
            else -> false
        }
    }
}

@TaskerInputRoot
open class Input(
        @field:TaskerInputField("key") open var key: String? = null,
        @field:TaskerInputField("value") open var value: String? = null,
        @field:TaskerInputField("type") open var type: Type? = null)

enum class Type : Serializable {
    GLOBAL,
    SECURE,
    SYSTEM
}