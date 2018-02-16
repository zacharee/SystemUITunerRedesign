package com.zacharee1.systemuituner.activites.apppickers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.dinuscxj.progressbar.CircleProgressBar
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.AppInfo
import com.zacharee1.systemuituner.misc.CustomAdapter
import com.zacharee1.systemuituner.util.Utils
import java.util.*

class AppsListActivity : AppCompatActivity() {

    private val appInfo: ArrayList<AppInfo>
        get() {
            val appMap = TreeMap<String, AppInfo>()

            val apps = Utils.getInstalledApps(this)

            val bar = findViewById<CircleProgressBar>(R.id.progress)

            for (info in apps) {
                try {
                    if (packageManager.getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.size > 1) {
                        appMap[info.loadLabel(packageManager).toString()] = AppInfo(info.loadLabel(packageManager).toString(),
                                info.packageName,
                                null,
                                info.loadIcon(packageManager))

                        runOnUiThread { bar.progress = 100 * (apps.indexOf(info) + 1) / apps.size }
                    }
                } catch (e: Exception) {
                }

            }

            return ArrayList(appMap.values)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_list)
        setTitle(R.string.select_app)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent == null) finish()

        val extras = intent!!.extras
        if (extras == null) finish()

        val isLeft = extras!!.getBoolean("isLeft")

        val recyclerView = findViewById<RecyclerView>(R.id.app_rec)

        Thread(Runnable {
            val adapter = CustomAdapter(appInfo, this@AppsListActivity, isLeft)

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
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}
