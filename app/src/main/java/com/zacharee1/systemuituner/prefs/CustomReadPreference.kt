package com.zacharee1.systemuituner.prefs

import android.app.AlertDialog
import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R

class CustomReadPreference : EditTextPreference {
    companion object {
        const val UNDEFINED = -1
        const val GLOBAL = 0
        const val SECURE = 1
        const val SYSTEM = 2
    }

    var type = UNDEFINED

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr, null)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, null, null)
    }
    constructor(context: Context) : super(context) {
        init(null, null, null)
    }

    init {
        isPersistent = true
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int?, defStyleRes: Int?) {
        layoutResource = R.layout.custom_read_preference

        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs,
                    R.styleable.CustomInputPreference,
                    defStyleAttr ?: 0,
                    defStyleRes ?: 0)

            for (i in 0 until array.indexCount) {
                val index = array.getIndex(i)

                when (index) {
                    R.styleable.CustomInputPreference_type -> type = array.getInteger(index, UNDEFINED)
                }
            }
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)
        updateSummary(text)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder?.itemView?.findViewById<ImageView>(R.id.select)?.setOnClickListener {
            if (text != null && summary != null) {
                val dialog = AlertDialog.Builder(context)
                        .setTitle(text)
                        .setMessage(summary)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                dialog?.window?.decorView?.findViewById<TextView>(android.R.id.message)?.setTextIsSelectable(true)
            }
        }
    }

    override fun setText(text: String?) {
        super.setText(text)

        updateSummary(text)
    }

    private fun updateSummary(key: String?) {
        summary = try {
            when (type) {
                GLOBAL -> Settings.Global.getString(context.contentResolver, key)
                SECURE -> Settings.Secure.getString(context.contentResolver, key)
                SYSTEM -> Settings.System.getString(context.contentResolver, key)
                else -> null
            }
        } catch (e: NullPointerException) {
            null
        }
    }
}
