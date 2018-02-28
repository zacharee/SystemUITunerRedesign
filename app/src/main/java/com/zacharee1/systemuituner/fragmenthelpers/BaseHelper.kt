package com.zacharee1.systemuituner.fragmenthelpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.Preference
import android.preference.PreferenceManager
import android.preference.PreferenceScreen

import com.zacharee1.systemuituner.fragments.ItemDetailFragment

abstract class BaseHelper(val fragment: ItemDetailFragment) {

    val context: Context?
        get() = fragment.context

    val activity: Activity?
        get() = fragment.activity

    val sharedPreferences: SharedPreferences
        get() = fragment.preferenceManager.sharedPreferences

    val preferenceScreen: PreferenceScreen
        get() = fragment.preferenceScreen

    val preferenceManager: PreferenceManager
        get() = fragment.preferenceManager

    val resources: Resources?
        get() = activity?.resources

    fun findPreference(preference: String): Preference? {
        return fragment.findPreference(preference)
    }

    fun startActivity(intent: Intent) {
        activity?.startActivity(intent)
    }

    fun startActivityForResult(intent: Intent, requestCode: Int) {
        activity?.startActivityForResult(intent, requestCode)
    }

    fun sendBroadcast(intent: Intent) {
        activity?.sendBroadcast(intent)
    }

    open fun onResume() {

    }

    abstract fun onDestroy()
}
