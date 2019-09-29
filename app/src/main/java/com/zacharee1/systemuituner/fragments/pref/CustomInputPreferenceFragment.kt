package com.zacharee1.systemuituner.fragments.pref

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceDialogFragmentCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.CustomInputPreference
import kotlinx.android.synthetic.main.custom_input_preference_layout.view.*

open class CustomInputPreferenceFragment : PreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(key: String): CustomInputPreferenceFragment {
            val frag = CustomInputPreferenceFragment()
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
        val split = value?.split(CustomInputPreference.SEPARATOR) ?: return

        keyText = split[0]
        valueText = if (split[1] == "null") null else split[1]
    }
}