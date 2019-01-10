package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.os.Bundle
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
import kotlinx.android.synthetic.main.custom_input_preference_layout.view.*

open class CustomInputPreference : DialogPreference {
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

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)

        dialogTitle = title
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

    open fun handleSave(keyContent: String?, valueText: String?) {
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

    fun getPersistedString() = preferenceDataStore?.getString(key, null)

    open class Fragment : PreferenceDialogFragmentCompat() {
        companion object {
            fun newInstance(key: String): Fragment {
                val frag = Fragment()
                frag.arguments = Bundle().apply { putString(ARG_KEY, key) }
                return frag
            }
        }

        val layout: View by lazy { LayoutInflater.from(context)
                .inflate(R.layout.custom_input_preference_layout, null, false) }
        val keyBox: EditText by lazy { layout.findViewById<EditText>(R.id.key) }
        val valueBox: EditText by lazy { layout.findViewById<EditText>(R.id.value) }

        var keyText: String?
            get() = keyBox.text?.toString()
            set(value) {
                keyBox.setText(value)
            }
        var valueText: String?
            get() = valueBox.text?.toString()
            set(value) {
                valueBox.setText(value)
            }

        open val keyHint by lazy { resources.getString(R.string.key) }
        open val valueHint by lazy { resources.getString(R.string.value_plaintext) }

        override fun onCreateDialogView(context: Context?): View {
            update((preference as CustomInputPreference).getPersistedString())
            return layout
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            if (positiveResult) {
                (preference as CustomInputPreference).apply {
                    handleSave(keyText, valueText)
                }
            }
        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)

            view.key_holder.hint = keyHint
            view.value_holder.hint = valueHint
        }

        private fun update(value: String?) {
            val split = value?.split(SEPARATOR) ?: return

            keyText = split[0]
            valueText = if (split[1] == "null") null else split[1]
        }
    }
}
