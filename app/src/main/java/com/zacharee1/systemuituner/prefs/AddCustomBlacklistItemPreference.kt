package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.CustomBlacklistInfo
import com.zacharee1.systemuituner.util.prefs

class AddCustomBlacklistItemPreference : CustomInputPreference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        key = "add_custom_item"
        title = context.resources.getString(R.string.add_item)
        dialogTitle = title
    }

    override fun handleSave(keyContent: String?, valueText: String?) {
        if (keyContent == null || valueText == null) return

        context.prefs.addCustomBlacklistItem(
                CustomBlacklistInfo(keyContent, valueText))
    }

    class Fragment : CustomInputPreference.Fragment() {
        companion object {
            fun newInstance(key: String): Fragment {
                val frag = Fragment()
                frag.arguments = Bundle().apply { putString(ARG_KEY, key) }
                return frag
            }
        }

        override val valueHint by lazy { resources.getString(R.string.name) }
    }
}