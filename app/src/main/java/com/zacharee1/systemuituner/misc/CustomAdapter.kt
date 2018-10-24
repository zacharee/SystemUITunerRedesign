package com.zacharee1.systemuituner.misc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rey.material.widget.CheckedImageView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.apppickers.AppsListActivity
import com.zacharee1.systemuituner.activites.apppickers.ComponentsListActivity
import com.zacharee1.systemuituner.util.writeSecure
import java.util.*

class CustomAdapter(private val apps: ArrayList<AppInfo>,
                    private val context: Context,
                    private val isLeft: Boolean,
                    private val singleSelect: Boolean) : RecyclerView.Adapter<CustomAdapter.CustomHolder>() {

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomHolder {
        val view = LayoutInflater.from(context).inflate(if (singleSelect) R.layout.app_info_single else R.layout.app_info_multi, parent, false)
        return CustomHolder(view)
    }

    override fun onBindViewHolder(holder: CustomHolder, position: Int) {
        holder.setAppInfo(apps[position])
    }

    inner class CustomHolder internal constructor(var view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var appInfo: AppInfo

        fun setAppInfo(info: AppInfo) {
            appInfo = info

            setAppName()
            setAppIcon()
            setAppSummary()
            setListener()
        }

        private fun setAppName() {
            val name = view.findViewById<TextView>(R.id.app_name)
            name.text = appInfo.appName
        }

        private fun setAppIcon() {
            val icon = view.findViewById<ImageView>(R.id.app_icon)
            icon.setImageDrawable(appInfo.appIcon)
        }

        private fun setAppSummary() {
            val summary = view.findViewById<TextView>(R.id.summary)
            summary.text = appInfo.componentName
        }

        private fun setListener() {
            view.setOnClickListener {
                val check = view.findViewById<CheckedImageView>(R.id.checkbox)
                check.isSelected = true

                if (context is AppsListActivity) {
                    val activity = Intent(context, ComponentsListActivity::class.java)
                    activity.putExtra("package", appInfo.packageName)
                    activity.putExtra("name", appInfo.appName)
                    activity.putExtra("isLeft", isLeft)

                    (context as Activity).startActivityForResult(activity, 1337)
                } else if (context is ComponentsListActivity) {
                    context.writeSecure(
                            if (isLeft) "sysui_keyguard_left" else "sysui_keyguard_right",
                            appInfo.packageName + "/" + appInfo.componentName
                    )
                    val activity = context as Activity
                    activity.setResult(Activity.RESULT_OK)
                    activity.finish()
                }
            }
        }
    }
}
