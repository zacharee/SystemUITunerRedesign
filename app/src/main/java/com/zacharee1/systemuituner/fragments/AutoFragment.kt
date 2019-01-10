package com.zacharee1.systemuituner.fragments

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.util.changeBlacklist
import com.zacharee1.systemuituner.util.hasUsage
import com.zacharee1.systemuituner.util.updateBlacklistSwitches
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

class AutoFragment : AnimFragment() {
    override val prefsRes = R.xml.pref_auto

    private val prefs = TreeMap<String, Preference>()

    private val content by lazy { activity!!.findViewById<ConstraintLayout>(R.id.content_main) }
    private val progress by lazy { content.findViewById<View>(R.id.progress) }

    private var job: Job? = null

    override fun onSetTitle() = resources.getString(R.string.auto_detect)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verifyUsage()
    }

    override fun onDestroy() {
        super.onDestroy()

        Thread {
            try {
                job?.cancel()
            } catch (e: Exception) {}
        }.start()

        content?.removeView(progress)
    }


    private fun verifyUsage() {
        if (activity?.hasUsage() == true) setUp()
        else {
            SetupActivity.make(context!!, arrayListOf(Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS))
        }
    }

    private fun setUp() {
        LayoutInflater.from(activity).inflate(R.layout.indet_circle_prog, content, true)

        job = GlobalScope.launch {
            val dump = Shell.sh("dumpsys activity service com.android.systemui/.SystemUIService")
                    .exec()
                    .run { out.apply {addAll(err) } }
                    .run { TextUtils.join("\n", this) }

            dump?.let {
                val index = dump.indexOf("icon slots")
                if (index != -1) {
                    val icons = dump.substring(index)
                    val ico = ArrayList(icons.split("\n"))
                    ico.removeAt(0)
                    for (slot in ico) {
                        if (slot.startsWith("         ") || slot.startsWith("        ")) {
                            val p = Pattern.compile("\\((.*?)\\)")
                            val m = p.matcher(slot)

                            while (!m.hitEnd()) {
                                if (activity == null) return@launch
                                if (m.find()) {
                                    val result = m.group().replace("(", "").replace(")", "")

                                    val preference = SwitchPreference(context)
                                    preference.title = result
                                    preference.key = result
                                    preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                                        context?.changeBlacklist(preference.key, o.toString().toBoolean())
                                        true
                                    }

                                    prefs[preference.key] = preference
                                    break
                                }
                            }
                        } else
                            break
                    }
                }

                val p = Pattern.compile("slot=(.+?)\\s")
                val m = p.matcher(dump)
                var find = ""

                while (!m.hitEnd()) if (m.find()) find = find + m.group() + "\n"

                val slots = ArrayList(find.split("\n"))
                for (slot in slots) {
                    val slotNew = slot.replace("slot=", "").replace(" ", "")

                    val preference = SwitchPreference(context)
                    preference.title = slotNew
                    preference.key = slotNew
                    preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                        context?.changeBlacklist(preference.key, o.toString().toBoolean())
                        true
                    }

                    if (!preference.key.isBlank() && !preference.title.toString().isBlank()) {
                        prefs[preference.key] = preference
                    }
                }

                if (prefs.values.isNotEmpty()) {
                    for (preference in prefs.values) {
                        activity!!.runOnUiThread { preferenceScreen.addPreference(preference) }
                    }
                } else {
                    val notSupported = Preference(activity)
                    notSupported.setSummary(R.string.feature_not_supported)
                    notSupported.isSelectable = false
                    activity!!.runOnUiThread { preferenceScreen.addPreference(notSupported) }
                }

                activity?.runOnUiThread {
                    updateBlacklistSwitches()
                }
            }

            activity?.runOnUiThread {
                content?.removeView(progress)
            }
        }
    }
}