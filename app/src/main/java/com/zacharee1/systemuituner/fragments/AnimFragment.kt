package com.zacharee1.systemuituner.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.prefs.AddCustomBlacklistItemPreference
import com.zacharee1.systemuituner.prefs.CustomInputPreference
import com.zacharee1.systemuituner.util.prefs
import jp.wasabeef.recyclerview.animators.LandingAnimator
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceFragment

abstract class AnimFragment : CollapsiblePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    internal abstract val prefsRes: Int

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(prefsRes, rootKey)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = onSetTitle() ?: return
        activity?.prefs?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.prefs?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is AddCustomBlacklistItemPreference -> {
                val frag = AddCustomBlacklistItemPreference.Fragment.newInstance(preference.key)
                frag.setTargetFragment(this, 1)
                frag.show(fragmentManager!!, null)
            }
            is CustomInputPreference -> {
                val frag = CustomInputPreference.Fragment.newInstance(preference.key)
                frag.setTargetFragment(this, 0)
                frag.show(fragmentManager!!, null)
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onCreateRecyclerView(inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?): RecyclerView {
        val recView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)

        recView.itemAnimator = object : LandingAnimator() {

        }.apply {
            addDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            changeDuration = 0
            removeDuration = 0
            moveDuration = addDuration
        }

        return recView
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {}

    internal abstract fun onSetTitle(): String?
}