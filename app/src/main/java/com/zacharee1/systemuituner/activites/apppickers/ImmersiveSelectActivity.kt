package com.zacharee1.systemuituner.activites.apppickers

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.misc.AppInfo
import com.zacharee1.systemuituner.util.Utils
import java.util.*

class ImmersiveSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark_NoActionBar else R.style.AppTheme_NoActionBar)

        setContentView(R.layout.activity_blank_custom_toolbar)
        setTitle(R.string.select_apps)

        //        final ProgressBar bar = new ProgressBar(this);
        //        bar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //        ((LinearLayout)findViewById(R.id.content_main)).addView(bar);

        val installedApps = Utils.getInstalledApps(this)

        val bar = findViewById<ProgressBar>(R.id.app_load_progress)
        bar.max = installedApps.size

        Thread(Runnable {
            Looper.prepare()

            val appMap = TreeMap<String, AppInfo>()

            for (info in installedApps) {
                try {
                    if (packageManager.getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.size > 1) {
                        appMap[info.loadLabel(packageManager).toString()] = AppInfo(info.loadLabel(packageManager).toString(),
                                info.packageName,
                                null,
                                info.loadIcon(packageManager))
                        runOnUiThread { bar.progress = installedApps.indexOf(info) + 1 }
                    }
                } catch (e: Exception) {
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
        }).start()
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
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

    class SelectorFragment : PreferenceFragment() {
        private var mInfo: TreeMap<String, AppInfo>? = null

        fun setInfo(info: TreeMap<String, AppInfo>) {
            mInfo = info
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_blank)
            populateList()
        }

        private fun populateList() {
            val selectedApps = ImmersiveHandler.parseSelectedApps(activity, TreeSet())

            if (mInfo != null) {
                for (info in mInfo!!.values) {
                    val preference = CheckBoxPreference(activity)
                    preference.title = info.appName
                    preference.summary = info.packageName
                    preference.icon = info.appIcon
                    preference.key = info.packageName
                    preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                        val isChecked = java.lang.Boolean.valueOf(o.toString())
                        if (isChecked) {
                            ImmersiveHandler.addApp(activity, preference.key)
                        } else {
                            ImmersiveHandler.removeApp(activity, preference.key)
                        }
                        restartMode()
                        true
                    }
                    preference.isChecked = selectedApps.contains(preference.key)

                    preferenceScreen.addPreference(preference)
                }
            }
        }

        fun selectAllBoxes() {
            setBoxesSelected(true)
        }

        fun deselectAllBoxes() {
            setBoxesSelected(false)
        }

        private fun setBoxesSelected(selected: Boolean) {
            for (i in 0 until preferenceScreen.preferenceCount) {
                val p = preferenceScreen.getPreference(i)

                if (p is CheckBoxPreference) {
                    p.isChecked = selected
                    p.getOnPreferenceChangeListener().onPreferenceChange(p, selected)
                }
            }
        }

        fun invertSelection() {
            val selected = ArrayList<CheckBoxPreference>()
            val unselected = ArrayList<CheckBoxPreference>()

            (0 until preferenceScreen.preferenceCount)
                    .map { preferenceScreen.getPreference(it) }
                    .filterIsInstance<CheckBoxPreference>()
                    .forEach {
                        if (it.isChecked) {
                            selected.add(it)
                        } else {
                            unselected.add(it)
                        }
                    }

            for (box in selected) {
                box.isChecked = false
                box.onPreferenceChangeListener.onPreferenceChange(box, false)
            }

            for (box in unselected) {
                box.isChecked = true
                box.onPreferenceChangeListener.onPreferenceChange(box, true)
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
