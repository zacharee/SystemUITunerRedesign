package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem

class CustomInputPreference : DialogPreference {
    companion object {
        const val UNDEFINED = -1
        const val GLOBAL = 0
        const val SECURE = 1
        const val SYSTEM = 2

        const val SEPARATOR = "#$%"
        const val EXTRA_VALUE = "value"
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
        fragment = "${context.packageName}/${Fragment::class.java.name}"
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

    override fun onSetInitialValue(defaultValue: Any?) {
        extras.putString(EXTRA_VALUE, getPersistedString(null))
    }

    fun handleSave(keyContent: String?, valueText: String?) {
        var valueContent = valueText
        if (valueContent != null && (valueContent.isEmpty() || valueContent.isBlank())) valueContent = null

        when (type) {
            GLOBAL -> context.writeGlobal(keyContent, valueContent)
            SECURE -> context.writeSecure(keyContent, valueContent)
            SYSTEM -> context.writeSystem(keyContent, valueContent)
        }

        val string = "$keyContent$SEPARATOR$valueContent"
        persistString(string)
    }

    class Fragment : PreferenceDialogFragmentCompat() {
        private val layout by lazy { LayoutInflater.from(context).inflate(R.layout.custom_input_preference_layout, null, false) }
        private val keyBox by lazy { layout.findViewById<EditText>(R.id.key) }
        private val valueBox by lazy { layout.findViewById<EditText>(R.id.value) }

        private var keyText: String?
            get() = keyBox.text.toString()
            set(value) {
                keyBox.setText(value)
            }
        private var valueText: String?
            get() = valueBox.text.toString()
            set(value) {
                valueBox.setText(value)
            }

        init {
            update(preference.extras.getString(EXTRA_VALUE))
        }

        override fun onCreateDialogView(context: Context?): View {
            return layout
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            if (positiveResult) {
                (preference as CustomInputPreference).apply {
                    handleSave(keyText, valueText)
                }
            }
        }

        fun update(value: String?) {
            val split = value?.split(SEPARATOR)

            if (split != null) {
                keyText = split[0]
                valueText = if (split[1] == "null") null else split[1]
            }
        }
    }
}
