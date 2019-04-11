package com.zacharee1.systemuituner.fragments.pref

import android.os.Bundle
import com.zacharee1.systemuituner.R

class AddCustomBlacklistItemPreferenceFragment : CustomInputPreferenceFragment() {
    companion object {
        fun newInstance(key: String): AddCustomBlacklistItemPreferenceFragment {
            val frag = AddCustomBlacklistItemPreferenceFragment()
            frag.arguments = Bundle().apply { putString(ARG_KEY, key) }
            return frag
        }
    }

    override val valueHint by lazy { resources.getString(R.string.name) }
}