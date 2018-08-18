package com.zacharee1.systemuituner.fragments

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.preference.Preference
import android.preference.SwitchPreference
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.LayoutInflater
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.util.changeBlacklist
import com.zacharee1.systemuituner.util.hasUsage
import com.zacharee1.systemuituner.util.runCommand
import com.zacharee1.systemuituner.util.updateBlacklistSwitches
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.regex.Pattern

class AutoFragment : AnimFragment(), AppOpsManager.OnOpChangedListener {
    private val prefs = TreeMap<String, Preference>()
    private val appOpsManager by lazy { activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager }

    private lateinit var observable: Disposable

    override fun onSetTitle() = resources.getString(R.string.auto_detect)

    override fun onAnimationFinishedEnter(enter: Boolean) {
        appOpsManager.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS, activity.packageName, this)
        verifyUsage(enter)
    }

    override fun onOpChanged(op: String?, packageName: String?) {
        when (op) {
            AppOpsManager.OPSTR_GET_USAGE_STATS -> verifyUsage(true)
        }
    }

    override fun onAnimationCreated(enter: Boolean) {
        val content = activity.findViewById<ConstraintLayout>(R.id.content_main)

        if (!enter) {
            Thread {
                try {
                    observable.dispose()
                } catch (e: Exception) {}
            }.start()

            content.removeView(content.findViewById(R.id.progress))
        }
    }

    private fun verifyUsage(enter: Boolean) {
        if (activity.hasUsage()) setUp(enter)
        else {
            SetupActivity.make(context, arrayListOf(Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS))
        }
    }

    private fun setUp(enter: Boolean) {
        val content = activity.findViewById<ConstraintLayout>(R.id.content_main)

        if (enter) {
            LayoutInflater.from(activity).inflate(R.layout.indet_circle_prog, content, true)

            addPreferencesFromResource(R.xml.pref_auto)

            observable = Observable.fromCallable { runCommand("dumpsys activity service com.android.systemui/.SystemUIService") }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe { dump ->
                        dump?.let {
                            Log.e("SystemUITuner", dump)
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
                                            if (activity == null) return@subscribe
                                            if (m.find()) {
                                                val result = m.group().replace("(", "").replace(")", "")

                                                val preference = SwitchPreference(context)
                                                preference.title = result
                                                preference.key = result
                                                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                                                    context.changeBlacklist(preference.key, o.toString().toBoolean())
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
                                    context.changeBlacklist(preference.key, o.toString().toBoolean())
                                    true
                                }

                                if (!preference.key.isBlank() && !preference.title.toString().isBlank()) {
                                    prefs[preference.key] = preference
                                }
                            }

                            if (prefs.values.isNotEmpty()) {
                                for (preference in prefs.values) {
                                    preferenceScreen.addPreference(preference)
                                }
                            } else {
                                val notSupported = Preference(activity)
                                notSupported.setSummary(R.string.feature_not_supported)
                                notSupported.isSelectable = false
                                preferenceScreen.addPreference(notSupported)
                            }

                            activity.runOnUiThread {
                                updateBlacklistSwitches()
                            }
                        }

                        activity.runOnUiThread {
                            content.removeView(content.findViewById(R.id.progress))
                        }
                    }
        }
    }
}