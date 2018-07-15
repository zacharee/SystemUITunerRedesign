package com.zacharee1.systemuituner.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.preference.Preference
import android.preference.SwitchPreference
import android.provider.Settings
import android.widget.Toast
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsUtils
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

open class StatbarFragment : AnimFragment() {
    companion object {
        const val RESET_BLACKLIST = "reset_blacklist"
        const val BACKUP_BLACKLIST = "backup_blacklist"
        const val RESTORE_BLACKLIST = "restore_blacklist"
        const val ICON_BLACKLIST = "icon_blacklist"
        const val ICON_BLACKLIST_BACKUP = "icon_blacklist_backup"
        const val AUTO_DETECT = "auto_detect"

        const val BR_REQ = 1011
        const val BW_REQ = 1012
    }

    override fun onSetTitle() = resources.getString(R.string.status_bar)

    override fun onAnimationFinishedEnter(enter: Boolean) {
        if (enter) {
            addPreferencesFromResource(R.xml.pref_statbar)
            preferenceListeners()
            setSwitchPreferenceStates()
            switchPreferenceListeners()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BR_REQ) {
                data?.data?.apply { parseBackupFile(this) }
            }

            if (requestCode == BW_REQ) {
                data?.data?.apply { writeBackupFile(this) }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    internal fun preferenceListeners() {
        val resetBL = findPreference(RESET_BLACKLIST)
        val backupBL = findPreference(BACKUP_BLACKLIST)
        val restoreBL = findPreference(RESTORE_BLACKLIST)
        val auto = findPreference(AUTO_DETECT)

        resetBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.writeSecure(context, ICON_BLACKLIST, "")
            setSwitchPreferenceStates()
            true
        }

        backupBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val createIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            val format = SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())

            createIntent.addCategory(Intent.CATEGORY_OPENABLE)
            createIntent.type = "*/*"
            createIntent.putExtra(Intent.EXTRA_TITLE, "${format.format(Date())}.blacklist")
            startActivityForResult(createIntent, BW_REQ)
            true
        }

        restoreBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val searchIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            searchIntent.addCategory(Intent.CATEGORY_OPENABLE)
            searchIntent.type = "*/*"
            startActivityForResult(searchIntent, BR_REQ)
            true
        }

        auto?.setOnPreferenceClickListener {
            val fragment = AutoFragment()
            fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.content_main, fragment)
                    ?.addToBackStack("auto")
                    ?.commit()
            true
        }
    }

    private fun setSwitchPreferenceStates() {
        SettingsUtils.shouldSetSwitchChecked(this)
    }

    internal fun switchPreferenceListeners() {
        (0 until preferenceScreen.rootAdapter.count)
                .map { preferenceScreen.rootAdapter.getItem(it) }
                .filterIsInstance<SwitchPreference>()
                .forEach {
                    it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                        val key = preference.key
                        val value = java.lang.Boolean.valueOf(o.toString())

                        SettingsUtils.changeBlacklist(key, value, context)
                        true
                    }
                }
    }

    private fun parseBackupFile(uri: Uri) {
        val stream = activity.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()

        while (true) {
            val line = reader.readLine() ?: break
            builder.append(line)
        }

        val bl = builder.toString()

        bl.toCharArray().forEach {
            val valid = it in 'a'..'z'
                    || it in 'A'..'Z'
                    || it == ','
                    || it == '_'
            if (!valid) {
                Toast.makeText(activity, R.string.invalid_blacklist_backup, Toast.LENGTH_SHORT).show()
                return
            }
        }

        Toast.makeText(activity, R.string.backup_restored, Toast.LENGTH_SHORT).show()

        stream.close()
        SettingsUtils.writeSecure(activity, ICON_BLACKLIST, bl)
        setSwitchPreferenceStates()
    }

    private fun writeBackupFile(uri: Uri) {
        val blacklist = Settings.Secure.getString(activity.contentResolver, ICON_BLACKLIST)
        val descriptor = activity.contentResolver.openFileDescriptor(uri, "w")
        val stream = FileOutputStream(descriptor.fileDescriptor)

        stream.write(blacklist.toByteArray())
        stream.close()
        descriptor.close()
    }
}