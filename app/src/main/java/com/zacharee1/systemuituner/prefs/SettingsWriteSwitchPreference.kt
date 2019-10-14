package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem

class SettingsWriteSwitchPreference(context: Context, attributeSet: AttributeSet) : SwitchPreference(context, attributeSet), Preference.OnPreferenceChangeListener {
    private enum class Type {
        GLOBAL,
        SECURE,
        SYSTEM
    }

    private var type = Type.GLOBAL
    private var enabledValue: String? = null
    private var disabledValue: String? = null

    private var externalListener: OnPreferenceChangeListener? = null

    private val shouldBeChecked: Boolean
        get() {
            return when (type) {
                Type.GLOBAL -> Settings.Global.getString(context.contentResolver, key) == enabledValue
                Type.SECURE -> Settings.Secure.getString(context.contentResolver, key) == enabledValue
                Type.SYSTEM -> Settings.System.getString(context.contentResolver, key) == enabledValue
            }
        }

    init {
        val array = context.theme.obtainStyledAttributes(attributeSet,
                R.styleable.SettingsWriteSwitchPreference,
                0,
                0)

        for (i in 0 until array.indexCount) {
            when (val index = array.getIndex(i)) {
                R.styleable.SettingsWriteSwitchPreference_type -> type = when (array.getInteger(index, -1)) {
                    0 -> Type.GLOBAL
                    1 -> Type.SECURE
                    2 -> Type.SYSTEM
                    else -> throw IllegalArgumentException("type must be set")
                }
                R.styleable.SettingsWriteSwitchPreference_enabled_value -> enabledValue = array.getString(index)
                R.styleable.SettingsWriteSwitchPreference_disabled_value -> disabledValue = array.getString(index)
            }
        }

        isChecked = shouldBeChecked

        super.setOnPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val value = if (isChecked) enabledValue else disabledValue

        val ret = when (type) {
            Type.GLOBAL -> context.writeGlobal(key, value)
            Type.SECURE -> context.writeSecure(key, value)
            Type.SYSTEM -> context.writeSystem(key, value)
        }

        return externalListener?.onPreferenceChange(preference, newValue) != false && ret
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        externalListener = onPreferenceChangeListener
    }

    override fun getOnPreferenceChangeListener(): OnPreferenceChangeListener? {
        return externalListener
    }
}