package com.zacharee1.systemuituner.prefs

import android.content.Context
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
}