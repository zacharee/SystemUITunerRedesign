package com.zacharee1.systemuituner.fragmenthelpers

import android.Manifest
import android.content.Intent
import android.preference.Preference
import android.preference.SwitchPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.util.SettingsUtils
import com.zacharee1.systemuituner.util.Utils
import java.util.*
import java.util.regex.Pattern

class AutoHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {
    private val mPrefs = TreeMap<String, Preference>()

    init {

        val hasUsage = SettingsUtils.hasSpecificPerm(context, Manifest.permission.PACKAGE_USAGE_STATS)
        val hasDump = SettingsUtils.hasSpecificPerm(context, Manifest.permission.DUMP)

        if (hasDump && hasUsage) {
            val dump = Utils.runCommand("dumpsys activity service com.android.systemui/.SystemUIService")!!

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
                            if (m.find()) {
                                val result = m.group().replace("(", "").replace(")", "")

                                val preference = SwitchPreference(context)
                                preference.title = result
                                preference.key = result
                                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                                    SettingsUtils.changeBlacklist(preference.key, java.lang.Boolean.valueOf(o.toString()), context)
                                    true
                                }

                                mPrefs[preference.key] = preference
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
                    SettingsUtils.changeBlacklist(preference.key, o.toString().toBoolean(), context)
                    true
                }

                if (!preference.key.isEmpty() && !preference.title.toString().isEmpty()) {
                    mPrefs[preference.key] = preference
                }
            }

            if (mPrefs.values.isNotEmpty()) {
                for (preference in mPrefs.values) {
                    preferenceScreen.addPreference(preference)
                }
            } else {
                val notSupported = Preference(activity)
                notSupported.setSummary(R.string.feature_not_supported)
                notSupported.isSelectable = false
                preferenceScreen.addPreference(notSupported)
            }

            SettingsUtils.shouldSetSwitchChecked(fragment)
        } else {
            val intent = Intent(context, SetupActivity::class.java)
            val perms = ArrayList<String>()
            if (!hasUsage) perms.add(Manifest.permission.PACKAGE_USAGE_STATS)
            if (!hasDump) perms.add(Manifest.permission.DUMP)

            intent.putExtra("permission_needed", perms.toTypedArray())
            startActivity(intent)

            activity?.finish()
        }
    }

    override fun onDestroy() {

    }
}
