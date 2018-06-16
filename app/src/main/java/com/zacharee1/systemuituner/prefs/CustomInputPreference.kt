package com.zacharee1.systemuituner.prefs

import android.app.AlertDialog
import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsUtils

class CustomInputPreference : DialogPreference {
    companion object {
        const val UNDEFINED = -1
        const val GLOBAL = 0
        const val SECURE = 1
        const val SYSTEM = 2
    }

    var type = UNDEFINED

    private lateinit var layout: View
    private lateinit var keyBox: EditText
    private lateinit var valueBox: EditText

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

    private fun init(attrs: AttributeSet?, defStyleAttr: Int?, defStyleRes: Int?) {
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

    override fun onCreateDialogView(): View {
        layout = LayoutInflater.from(context).inflate(R.layout.custom_input_preference_layout, null)
        keyBox = layout.findViewById(R.id.key)
        valueBox = layout.findViewById(R.id.value)
        return layout
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        builder.setPositiveButton(R.string.ok) { _, _ ->
            handleSave()
        }
        builder.setNegativeButton(R.string.cancel, null)
    }

    private fun handleSave() {
        val keyContent = keyBox.text
        val valueContent = valueBox.text

        if (keyContent == null) return

        when (type) {
            GLOBAL -> SettingsUtils.writeGlobal(context, keyContent.toString(), valueContent?.toString())
            SECURE -> SettingsUtils.writeSecure(context, keyContent.toString(), valueContent?.toString())
            SYSTEM -> SettingsUtils.writeSystem(context, keyContent.toString(), valueContent?.toString())
        }
    }
}
