package com.zacharee1.systemuituner.activites.apppickers

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.dinuscxj.progressbar.CircleProgressBar
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.misc.AppInfo
import com.zacharee1.systemuituner.util.getInstalledApps
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ImmersiveSelectActivity : BaseAnimActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.immersive_select)
        setTitle(R.string.select_apps)

        val bar = findViewById<CircleProgressBar>(R.id.app_load_progress)

        Observable.fromCallable { getInstalledApps() }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    val appMap = TreeMap<String, AppInfo>()

                    it.forEach { info ->
                        val activities = packageManager.getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities
                        if (activities?.isNotEmpty() == true) {
                            appMap[info.loadLabel(packageManager).toString()] = AppInfo(info.loadLabel(packageManager).toString(),
                                    info.packageName,
                                    null,
                                    info.loadIcon(packageManager))
                            runOnUiThread { bar.progress = 100 * (it.indexOf(info) + 1) / it.size }
                        }
                    }

                    runOnUiThread {
                        val fragment = SelectorFragment.newInstance()
                        fragment.setInfo(appMap)

                        (findViewById<View>(R.id.content_main) as LinearLayout).removeAllViews()
                        try {
                            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit()
                        } catch (e: Exception) {
                        }

                        setUpActionBar(fragment)
                    }
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar(fragment: SelectorFragment) {
        val selectAll = LayoutInflater.from(this).inflate(R.layout.select_all, toolbar, false)
        val deselectAll = LayoutInflater.from(this).inflate(R.layout.deselect_all, toolbar, false)
        val invertSelection = LayoutInflater.from(this).inflate(R.layout.invert_select, toolbar, false)

        toolbar.addView(selectAll)
        toolbar.addView(deselectAll)
        toolbar.addView(invertSelection)

        selectAll.setOnClickListener { fragment.selectAllBoxes() }
        deselectAll.setOnClickListener { fragment.deselectAllBoxes() }
        invertSelection.setOnClickListener { fragment.invertSelection() }

        selectAll.setOnLongClickListener {
            Toast.makeText(this@ImmersiveSelectActivity,
                    resources.getString(R.string.select_all),
                    Toast.LENGTH_SHORT)
                    .show()
            true
        }
        deselectAll.setOnLongClickListener {
            Toast.makeText(this@ImmersiveSelectActivity,
                    resources.getString(R.string.deselect_all),
                    Toast.LENGTH_SHORT)
                    .show()
            true
        }
        invertSelection.setOnLongClickListener {
            Toast.makeText(this@ImmersiveSelectActivity,
                    resources.getString(R.string.invert_selection),
                    Toast.LENGTH_SHORT)
                    .show()
            true
        }
    }

    class SelectorFragment : AnimFragment() {
        private var infos: TreeMap<String, AppInfo> = TreeMap()

        fun setInfo(info: TreeMap<String, AppInfo>) {
            infos.putAll(info)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_blank)
            populateList()
        }

        override fun onSetTitle(): String? = resources.getString(R.string.select_apps)

        private fun populateList() {
            val selectedApps = ImmersiveHandler.parseSelectedApps(activity, TreeSet())

            for (info in infos.values) {
                val preference = CheckBoxPreference(activity)
                preference.title = info.appName
                preference.summary = info.packageName
                preference.icon = info.appIcon
                preference.key = info.packageName
                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                    if (activity != null) {
                        val isChecked = o.toString().toBoolean()
                        if (isChecked) {
                            ImmersiveHandler.addApp(activity, preference.key)
                        } else {
                            ImmersiveHandler.removeApp(activity, preference.key)
                        }
                        restartMode()
                        true
                    } else false
                }
                preference.isChecked = selectedApps.contains(preference.key)

                preferenceScreen.addPreference(preference)
            }
        }

        fun selectAllBoxes() {
            setBoxesSelected(true)
        }

        fun deselectAllBoxes() {
            setBoxesSelected(false)
        }

        private fun setBoxesSelected(selected: Boolean) {
            (0 until preferenceScreen.preferenceCount)
                    .map { preferenceScreen.getPreference(it) }
                    .filterIsInstance<CheckBoxPreference>()
                    .apply {
                        forEach {
                            it.isChecked = selected
                        }
                    }
        }

        fun invertSelection() {
            (0 until preferenceScreen.preferenceCount)
                    .map { preferenceScreen.getPreference(it) }
                    .filterIsInstance<CheckBoxPreference>()
                    .apply {
                        forEach {
                            it.isChecked = !it.isChecked
                        }
                    }
        }

        private fun restartMode() {
            ImmersiveHandler.setMode(context, ImmersiveHandler.getMode(context))
        }

        companion object {

            fun newInstance(): SelectorFragment {
                return SelectorFragment()
            }
        }
    }
}
