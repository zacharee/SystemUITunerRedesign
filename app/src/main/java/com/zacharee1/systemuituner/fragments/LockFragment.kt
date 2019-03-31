package com.zacharee1.systemuituner.fragments

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.apppickers.AppsListActivity
import com.zacharee1.systemuituner.prefs.LockPref
import com.zacharee1.systemuituner.util.writeSecure

class LockFragment : AnimFragment() {
    override val prefsRes = R.xml.pref_lock

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        setEnabled()
        setLockIconStuff()
        setResetListeners()
    }

    override fun onResume() {
        super.onResume()
        setLockSummaryTitleAndIcon()
    }

    override fun onSetTitle() = resources.getString(R.string.lockscreen)

    private fun setEnabled() {
        val shortcuts = findPreference<PreferenceCategory>(LOCKSCREEN_SHORTCUTS)!!
        val oreoMsg = findPreference<Preference>(OREO_NEEDED)
        val isOreo = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1

        shortcuts.isEnabled = isOreo
        if (isOreo) shortcuts.removePreference(oreoMsg ?: return)
    }

    private fun setLockIconStuff() {
        setLockSummaryTitleAndIcon()

        val left = findPreference<Preference>(CHOOSE_LEFT)
        left?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val activity = Intent(context, AppsListActivity::class.java)
            activity.putExtra(EXTRA_ISLEFT, true)
            startActivityForResult(activity, 1337)
            true
        }

        val right = findPreference<Preference>(CHOOSE_RIGHT)
        right?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val activity = Intent(context, AppsListActivity::class.java)
            activity.putExtra(EXTRA_ISLEFT, false)
            startActivityForResult(activity, 1337)
            true
        }
    }

    private fun setLockSummaryTitleAndIcon() {
        val leftLock = findPreference<Preference>(CHOOSE_LEFT)
        val rightLock = findPreference<Preference>(CHOOSE_RIGHT)

        val leftSum = Settings.Secure.getString(context?.contentResolver, KEYGUARD_LEFT)
        val rightSum = Settings.Secure.getString(context?.contentResolver, KEYGUARD_RIGHT)

        var leftStuff: Array<String>? = null
        var rightStuff: Array<String>? = null

        if (leftSum != null) leftStuff = leftSum.split("/").toTypedArray()
        if (rightSum != null) rightStuff = rightSum.split("/").toTypedArray()

        leftLock?.summary = leftSum
        rightLock?.summary = rightSum

        try {
            leftLock?.title = context?.packageManager?.getApplicationLabel(context?.packageManager?.getApplicationInfo(leftStuff!![0], 0))
        } catch (e: Exception) {
            leftLock?.title = context?.resources?.getString(R.string.choose_left)
        }

        try {
            rightLock?.title = context?.packageManager?.getApplicationLabel(context?.packageManager?.getApplicationInfo(rightStuff!![0], 0))
        } catch (e: Exception) {
            rightLock?.title = context?.resources?.getString(R.string.choose_right)
        }

        val unknown = context?.resources?.getDrawable(R.drawable.ic_help_outline_black_24dp, null)?.mutate()
        unknown?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        try {
            leftLock?.icon = context?.packageManager?.getActivityIcon(ComponentName(leftStuff!![0], leftStuff[1]))
        } catch (e: Exception) {
            leftLock?.icon = unknown
        }

        try {
            rightLock?.icon = context?.packageManager?.getActivityIcon(ComponentName(rightStuff!![0], rightStuff[1]))
        } catch (e: Exception) {
            rightLock?.icon = unknown
        }

    }

    private fun setResetListeners() {
        val leftLock = findPreference<LockPref>(CHOOSE_LEFT)!!
        val rightLock = findPreference<LockPref>(CHOOSE_RIGHT)!!

        leftLock.resetListener = {
            context?.writeSecure(KEYGUARD_LEFT, null)
            setLockSummaryTitleAndIcon()
            true
        }
        rightLock.resetListener = {
            context?.writeSecure(KEYGUARD_RIGHT, null)
            setLockSummaryTitleAndIcon()
            true
        }
    }

    companion object {
        const val LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts"
        const val OREO_NEEDED = "oreo_needed"
        const val KEYGUARD_LEFT = "sysui_keyguard_left"
        const val KEYGUARD_RIGHT = "sysui_keyguard_right"
        const val CHOOSE_LEFT = "choose_left"
        const val CHOOSE_RIGHT = "choose_right"
        const val EXTRA_ISLEFT = "isLeft"
    }
}