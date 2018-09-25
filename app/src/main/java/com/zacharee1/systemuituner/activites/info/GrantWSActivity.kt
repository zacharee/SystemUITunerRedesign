package com.zacharee1.systemuituner.activites.info

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.Button
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.util.writeSystem

class GrantWSActivity : BaseAnimActivity(), AppOpsManager.OnOpChangedListener {
    companion object {
        private const val EXTRA_KEY = "key"
        private const val EXTRA_VALUE = "value"

        fun start(context: Context, key: String, value: Any?) {
            if (value !is Int
                    && value !is String
                    && value !is Float
                    && value !is Long) return

            val grant = Intent(context, GrantWSActivity::class.java)
            grant.putExtra(EXTRA_KEY, key)

            when (value) {
                is Int -> grant.putExtra(EXTRA_VALUE, value)
                is Float -> grant.putExtra(EXTRA_VALUE, value)
                is Long -> grant.putExtra(EXTRA_VALUE, value)
                is String -> grant.putExtra(EXTRA_VALUE, value)
            }

            context.startActivity(grant)
        }
    }

    private val aom by lazy { getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_grant_ws)
        aom.startWatchingMode(AppOpsManager.OPSTR_WRITE_SETTINGS, packageName, this)

        findViewById<Button>(R.id.grant).setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName")))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        aom.stopWatchingMode(this)
    }

    override fun onOpChanged(packageName: String, op: String) {
        if (packageName == this.packageName) {
            val granted = aom.checkOpNoThrow(AppOpsManager.OPSTR_WRITE_SETTINGS, Process.myUid(), this.packageName) == AppOpsManager.MODE_ALLOWED

            if (granted) writeSystem(intent.getStringExtra(EXTRA_KEY), intent.extras?.get(EXTRA_VALUE))

            finish()
        }
    }
}
