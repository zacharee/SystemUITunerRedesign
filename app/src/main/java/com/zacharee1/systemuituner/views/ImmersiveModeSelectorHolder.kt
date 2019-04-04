package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.zacharee1.systemuituner.R

class ImmersiveModeSelectorHolder : RadioGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        orientation = LinearLayout.VERTICAL

        val inflater = LayoutInflater.from(context)

        val none = inflater.inflate(R.layout.immersive_mode_selector, this, false) as RadioButton
        val full = inflater.inflate(R.layout.immersive_mode_selector, this, false) as RadioButton
        val status = inflater.inflate(R.layout.immersive_mode_selector, this, false) as RadioButton
        val nav = inflater.inflate(R.layout.immersive_mode_selector, this, false) as RadioButton
        val preconf = inflater.inflate(R.layout.immersive_mode_selector, this, false) as RadioButton

        none.id = R.id.immersive_none
        full.id = R.id.immersive_full
        status.id = R.id.immersive_status
        nav.id = R.id.immersive_nav
        preconf.id = R.id.immersive_preconf

        none.setText(R.string.none)
        full.setText(R.string.full)
        status.setText(R.string.status_bar)
        nav.setText(R.string.navigation_bar)
        preconf.setText(R.string.pre_confirms)

        addView(none)
        addView(full)
        addView(status)
        addView(nav)
        addView(preconf)
    }
}