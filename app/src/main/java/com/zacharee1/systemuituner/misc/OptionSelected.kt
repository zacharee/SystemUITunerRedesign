package com.zacharee1.systemuituner.misc

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.info.AboutActivity
import com.zacharee1.systemuituner.activites.settings.SettingsActivity

object OptionSelected {
    fun doAction(itemId: Int, context: Context): Boolean {
        when (itemId) {
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
                val teleIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/ZachareeTG"))
                context.startActivity(teleIntent)
                return true
            }
            R.id.action_xda_thread -> {
                val xdaIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forum.xda-developers.com/android/apps-games/app-systemui-tuner-t3588675"))
                context.startActivity(xdaIntent)
                return true
            }
            R.id.action_privacy_policy -> {
                val policyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zacharee/SystemUITunerRedesign/blob/master/privacy_policy.md"))
                context.startActivity(policyIntent)
                return true
            }
            R.id.action_terms -> {
                val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zacharee/SystemUITunerRedesign/blob/master/Terms.md"))
                context.startActivity(termsIntent)
                return true
            }
        }
        return false
    }
}
