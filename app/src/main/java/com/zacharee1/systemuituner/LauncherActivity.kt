package com.zacharee1.systemuituner

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.zacharee1.systemuituner.activites.info.IntroActivity
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.util.SuUtils
import com.zacharee1.systemuituner.util.Utils
import io.fabric.sdk.android.Fabric

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (preferences.getBoolean("show_intro", true)) {
            startActivity(Intent(this, IntroActivity::class.java))
        } else {
            val perms = arrayOf(Manifest.permission.WRITE_SECURE_SETTINGS, Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS)

            val ret = Utils.checkPermissions(this, perms)

            if (ret.isNotEmpty()) {
                if (SuUtils.testSudo()) {
                    SuUtils.sudo("pm grant com.zacharee1.systemuituner android.permission.WRITE_SECURE_SETTINGS ; " +
                            "pm grant com.zacharee1.systemuituner android.permission.DUMP ; " +
                            "pm grant com.zacharee1.systemuituner android.permission.PACKAGE_USAGE_STATS")
                    Utils.startUp(this)
                    finish()
                } else {
                    val intent = Intent(this, SetupActivity::class.java)
                    intent.putExtra("permission_needed", ret)
                    startActivity(intent)
                    finish()
                }
            } else {
                Utils.startUp(this)
                finish()
            }
        }

        finish()
    }
}
