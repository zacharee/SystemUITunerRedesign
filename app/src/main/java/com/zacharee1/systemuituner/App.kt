package com.zacharee1.systemuituner

import android.app.Application
import android.content.Context

import java.lang.ref.WeakReference

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = WeakReference(this)
    }

    companion object {
        private var mContext: WeakReference<Context>? = null

        val context: Context?
            get() = mContext!!.get()
    }
}
