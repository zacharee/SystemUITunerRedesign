package com.zacharee1.systemuituner.activites.apppickers

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.dinuscxj.progressbar.CircleProgressBar
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.misc.AppInfo
import com.zacharee1.systemuituner.misc.CustomAdapter
import java.util.*

class ComponentsListActivity : BaseAnimActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_list)
        setTitle(R.string.select_component)

        val intent = intent
        if (intent == null) {
            finish()
            return
        }

        val extras = intent.extras
        if (extras == null) {
            finish()
            return
        }

        val packageName = extras.getString("package")
        val appName = extras.getString("name")
        val isLeft = extras.getBoolean("isLeft")

        title = appName

        val recyclerView = findViewById<RecyclerView>(R.id.app_rec)

        Thread(Runnable {
            val adapter = CustomAdapter(getComponentInfo(packageName), this@ComponentsListActivity, isLeft, true)

            runOnUiThread {
                recyclerView.adapter = adapter
                findViewById<View>(R.id.progress).visibility = View.GONE
            }
        }).start()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getComponentInfo(packageName: String?): ArrayList<AppInfo> {
        val apps = TreeMap<String, AppInfo>()

        val pm = packageManager

        try {
            val info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)

            val activities = info.activities

            val bar = findViewById<CircleProgressBar>(R.id.progress)

            for (activity in activities) {
                apps[activity.name] = AppInfo(activity.name,
                        activity.packageName,
                        activity.name,
                        activity.loadIcon(packageManager))

                runOnUiThread { bar.progress = 100 * (Arrays.asList(*activities).indexOf(activity) + 1) / apps.size }
            }
        } catch (e: Exception) {
        }

        return ArrayList(apps.values)
    }
}
