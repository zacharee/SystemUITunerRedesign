package com.zacharee1.systemuituner.fragments.pref

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceDialogFragmentCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.CustomReadPreference
import kotlinx.android.synthetic.main.custom_input_preference_layout.view.*

open class CustomReadPreferenceFragment : PreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(key: String): CustomReadPreferenceFragment {
            val frag = CustomReadPreferenceFragment()
            frag.arguments = Bundle().apply { putString(ARG_KEY, key) }
            return frag
        }
    }

    val layout: View by lazy { LayoutInflater.from(context)
            .inflate(R.layout.custom_read_preference_layout, null, false) }
    val keyBox: EditText by lazy { layout.findViewById<EditText>(R.id.key) }

    var keyText: String?
        get() = keyBox.text?.toString()
        set(value) {
            keyBox.setText(value)
        }

    open val keyHint by lazy { resources.getString(R.string.key) }

    override fun onCreateDialogView(context: Context?): View {
        update((preference as CustomReadPreference).getPersistedString())
        return layout
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            (preference as CustomReadPreference).apply {
                handleSave(keyText)
            }
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        view.key_holder.hint = keyHint
    }

    private fun update(value: String?) {
        keyText = value
    }
}