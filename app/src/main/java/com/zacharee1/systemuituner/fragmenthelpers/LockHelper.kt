package com.zacharee1.systemuituner.fragmenthelpers

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.SwitchPreference
import android.provider.Settings
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.apppickers.AppsListActivity
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.util.SettingsUtils

class LockHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {

    init {
        setEnabled()
        setLockIconStuff()
        setShortcutSwitchListeners()
        setResetListeners()
    }

    override fun onDestroy() {

    }

    override fun onResume() {
        setLockSummaryAndIcon()
    }

    private fun setEnabled() {
        val shortcuts = findPreference(LOCKSCREEN_SHORTCUTS) as PreferenceCategory
        val oreoMsg = findPreference(OREO_NEEDED)
        val isOreo = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1

        shortcuts.isEnabled = isOreo
        if (isOreo) shortcuts.removePreference(oreoMsg)
    }

    private fun setShortcutSwitchListeners() {
        val left = findPreference(KEYGUARD_LEFT_UNLOCK) as SwitchPreference
        val right = findPreference(KEYGUARD_RIGHT_UNLOCK) as SwitchPreference
        val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
            SettingsUtils.writeSecure(context, preference.key, newValue.toString())
            true
        }

        left.onPreferenceChangeListener = listener
        right.onPreferenceChangeListener = listener
    }

    private fun setLockIconStuff() {
        setLockSummaryAndIcon()

        val left = findPreference(CHOOSE_LEFT)
        left?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val activity = Intent(context, AppsListActivity::class.java)
            activity.putExtra(EXTRA_ISLEFT, true)
            startActivityForResult(activity, 1337)
            true
        }

        val right = findPreference(CHOOSE_RIGHT)
        right?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val activity = Intent(context, AppsListActivity::class.java)
            activity.putExtra(EXTRA_ISLEFT, false)
            startActivityForResult(activity, 1337)
            true
        }
    }

    private fun setLockSummaryAndIcon() {
        val leftLock = findPreference(CHOOSE_LEFT)
        val rightLock = findPreference(CHOOSE_RIGHT)

        val leftSum = Settings.Secure.getString(context.contentResolver, KEYGUARD_LEFT)
        val rightSum = Settings.Secure.getString(context.contentResolver, KEYGUARD_RIGHT)

        var leftStuff: Array<String>? = null
        var rightStuff: Array<String>? = null

        if (leftSum != null) leftStuff = leftSum.split("/").toTypedArray()
        if (rightSum != null) rightStuff = rightSum.split("/").toTypedArray()

        leftLock?.summary = leftSum
        rightLock?.summary = rightSum

        val pm = activity?.packageManager

        val unknown = context.resources.getDrawable(R.drawable.ic_help_outline_black_24dp, null)
        unknown.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        try {
            leftLock?.icon = pm?.getActivityIcon(ComponentName(leftStuff!![0], leftStuff[1]))
        } catch (e: Exception) {
            leftLock?.icon = unknown
        }

        try {
            rightLock?.icon = pm?.getActivityIcon(ComponentName(rightStuff!![0], rightStuff[1]))
        } catch (e: Exception) {
            rightLock?.icon = unknown
        }

    }

    private fun setResetListeners() {
        val resetLeft = findPreference(RESET_LEFT)
        val resetRight = findPreference(RESET_RIGHT)

        resetLeft?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.writeSecure(context, KEYGUARD_LEFT, "")
            setLockSummaryAndIcon()
            true
        }
        resetRight?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.writeSecure(context, KEYGUARD_RIGHT, "")
            setLockSummaryAndIcon()
            true
        }
    }

    companion object {
        const val LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts"
        const val OREO_NEEDED = "oreo_needed"
        const val KEYGUARD_LEFT_UNLOCK = "sysui_keyguard_left_unlock"
        const val KEYGUARD_RIGHT_UNLOCK = "sysui_keyguard_right_unlock"
        const val KEYGUARD_LEFT = "sysui_keyguard_left"
        const val KEYGUARD_RIGHT = "sysui_keyguard_right"
        const val CHOOSE_LEFT = "choose_left"
        const val CHOOSE_RIGHT = "choose_right"
        const val EXTRA_ISLEFT = "isLeft"
        const val RESET_LEFT = "reset_left"
        const val RESET_RIGHT = "reset_right"
    }
}
