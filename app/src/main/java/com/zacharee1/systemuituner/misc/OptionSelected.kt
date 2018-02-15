package com.zacharee1.systemuituner.misc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MenuItem

import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.info.AboutActivity
import com.zacharee1.systemuituner.activites.settings.SettingsActivity

object OptionSelected {
    fun doAction(item: MenuItem, context: Context): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(context, SettingsActivity::class.java)
                context.startActivity(settingsIntent)
                return true
            }
            R.id.action_about -> {
                val aboutIntent = Intent(context, AboutActivity::class.java)
                context.startActivity(aboutIntent)
                return true
            }
            R.id.action_github -> {
                val ghIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/zacharee/SystemUITunerRedesign"))
                context.startActivity(ghIntent)
                return true
            }
            R.id.action_telegram -> {
                val teleIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/AAAAAEIB6WKWL-yphJbZwg"))
                context.startActivity(teleIntent)
                return true
            }
            R.id.action_g_plus -> {
                val gIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/113741695211107417994"))
                context.startActivity(gIntent)
                return true
            }
            R.id.action_xda_thread -> {
                val xdaIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forum.xda-developers.com/android/apps-games/app-systemui-tuner-t3588675"))
                context.startActivity(xdaIntent)
                return true
            }
        }
        return false
    }
}
