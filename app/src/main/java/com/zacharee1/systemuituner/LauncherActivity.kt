package com.zacharee1.systemuituner

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.activites.info.IntroActivity
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.util.SuUtils
import com.zacharee1.systemuituner.util.checkPermissions
import com.zacharee1.systemuituner.util.startUp
import io.fabric.sdk.android.Fabric

class LauncherActivity : BaseAnimActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (preferences.getBoolean("show_intro", true)) {
            startActivity(Intent(this, IntroActivity::class.java))
        } else {
            val perms = arrayListOf(Manifest.permission.WRITE_SECURE_SETTINGS, Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS)

            val ret = checkPermissions(perms)
            ret.removeAll(SetupActivity.NOT_REQUIRED)

            if (ret.isNotEmpty()) {
                if (SuUtils.testSudo()) {
                    SuUtils.sudo("pm grant $packageName ${Manifest.permission.WRITE_SECURE_SETTINGS} ; " +
                            "pm grant $packageName ${Manifest.permission.DUMP} ; " +
                            "pm grant $packageName ${Manifest.permission.PACKAGE_USAGE_STATS}")
                    startUp()
                    finish()
                } else {
                    SetupActivity.make(this, ret)
                    finish()
                }
            } else {
                startUp()
                finish()
            }
        }

        finish()
    }
}
