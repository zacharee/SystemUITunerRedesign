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

        const val SEPARATOR = "#$%"
    }

    var type = UNDEFINED

    private lateinit var layout: View
    private lateinit var keyBox: EditText
    private lateinit var valueBox: EditText

    private var keyText: String? = null
    private var valueText: String? = null

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

        isPersistent = true
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val persisted = getPersistedString(null) ?: return
        update(persisted)
    }

    override fun persistString(value: String?): Boolean {
        update(value)
        return super.persistString(value)
    }

    override fun onCreateDialogView(): View {
        layout = LayoutInflater.from(context).inflate(R.layout.custom_input_preference_layout, null)
        keyBox = layout.findViewById(R.id.key)
        valueBox = layout.findViewById(R.id.value)
        keyBox.setText(keyText)
        valueBox.setText(valueText)
        return layout
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            handleSave()
        }
        builder.setNegativeButton(R.string.cancel, null)
    }

    private fun handleSave() {
        val keyContent = keyBox.text?.toString() ?: return
        var valueContent = valueBox.text?.toString()

        if (valueContent != null && (valueContent.isEmpty() || valueContent.isBlank())) valueContent = null

        when (type) {
            GLOBAL -> SettingsUtils.writeGlobal(context, keyContent, valueContent)
            SECURE -> SettingsUtils.writeSecure(context, keyContent, valueContent)
            SYSTEM -> SettingsUtils.writeSystem(context, keyContent, valueContent)
        }

        val string = "$keyContent$SEPARATOR$valueContent"
        persistString(string)
    }

    private fun update(value: String?) {
        val split = value?.split(SEPARATOR)

        if (split != null) {
            keyText = split[0]
            valueText = if (split[1] == "null") null else split[1]
        }
    }
}
