package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import android.text.TextUtils

class BlacklistManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        const val ICON_BLACKLIST = "icon_blacklist"
        const val ICON_BLACKLIST_BACKUP = "icon_blacklist_backup"

        @SuppressLint("StaticFieldLeak")
        private var instance: BlacklistManager? = null

        fun getInstance(context: Context): BlacklistManager {
            if (instance == null) instance = BlacklistManager(context.applicationContext)

            return instance!!
        }
    }

    private fun String.listify() = split(",").toList()

    var currentBlacklist
        get() = Settings.Secure.getString(contentResolver, ICON_BLACKLIST) ?: ""
        set(value) {
            setCurrentBlacklist(value)
        }

    var currentBlacklistAsList
        get() = ArrayList(currentBlacklist.split(",").toList())
        set(value) {
            setCurrentBlacklist(value)
        }

    var backupBlacklist
        get() = Settings.Global.getString(contentResolver, ICON_BLACKLIST_BACKUP) ?: ""
        set(value) {
            writeGlobal(ICON_BLACKLIST_BACKUP, value)
        }

    fun setCurrentBlacklist(blacklist: String?) = writeSecure(ICON_BLACKLIST, blacklist)

    fun setCurrentBlacklist(blacklist: List<String>) = setCurrentBlacklist(
            TextUtils.join(",", blacklist.filterNot { it.isBlank() }))

    fun addItem(item: String): Boolean {
        return addItems(item.listify())
    }

    fun removeItem(item: String): Boolean {
        return removeItems(item.listify())
    }

    fun addItems(items: List<String>): Boolean {
        val current = currentBlacklistAsList

        current.addAll(items.distinct())

        return setCurrentBlacklist(current)
    }

    fun removeItems(items: List<String>): Boolean {
        val current = currentBlacklistAsList

        current.removeAll(items.distinct())

        return setCurrentBlacklist(current)
    }

    fun modifyItem(item: String, remove: Boolean): Boolean {
        return if (remove) removeItem(item)
        else addItem(item)
    }
}