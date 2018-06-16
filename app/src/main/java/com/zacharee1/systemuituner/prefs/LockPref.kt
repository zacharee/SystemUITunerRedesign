package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.zacharee1.systemuituner.R

class LockPref : Preference {
    private var widgetView: LinearLayout? = null

    var resetListener: (() -> Boolean)? = null
        set(value) {
            field = value
            widgetView?.visibility = if (value != null) View.VISIBLE else View.INVISIBLE
        }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onCreateView(parent: ViewGroup?): View {
        val view = super.onCreateView(parent)

        widgetView = view.findViewById(android.R.id.widget_frame)
        widgetView?.visibility = if (resetListener != null) View.VISIBLE else View.INVISIBLE

        val widgetButton = widgetView?.findViewById<ImageView>(R.id.reset)
        widgetButton?.setOnClickListener {
            val listener = resetListener
            if (listener == null || !listener.invoke()) {
                onClick()
            }
        }

        return view
    }
}
