package com.zacharee1.systemuituner.misc

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsUtils
import java.util.*
import java.util.regex.Pattern

class QSDragAdapter(private val mContext: Context) : RecyclerView.Adapter<QSDragAdapter.QSViewHolder>() {
    var mTiles = ArrayList<QSTile>()

    var mAvailableTiles = ArrayList<QSTile>()

    private val defaultTiles: ArrayList<QSTile>
        get() {
            val order = defaultTileOrder
            val array = order.split(",")

            return array.mapTo(ArrayList()) { QSTile(it, mContext) }
        }

    private val defaultTileOrder: String
        get() {
            val pm = mContext.packageManager

            return try {
                val resources = pm.getResourcesForApplication("com.android.systemui")
                val id = resources.getIdentifier("quick_settings_tiles_default", "string", "com.android.systemui")

                resources.getString(id)
            } catch (e: Exception) {
                ""
            }

        }

    init {
        parseTileList()
    }

    fun parseTileList() {
        var tiles: String? = Settings.Secure.getString(mContext.contentResolver, "sysui_qs_tiles")

        if (tiles == null) {
            tiles = defaultTileOrder
        }

        val tileArray = tiles.split(",")

        val tempTiles = tileArray.map { QSTile(it, mContext) }

        mTiles.clear()
        mTiles.addAll(tempTiles)

        refreshAvailableTiles()
    }

    private fun refreshAvailableTiles() {
        mAvailableTiles.clear()
        for (tile in defaultTiles) {
            val hasTile = mTiles.any { it.key == tile.key }

            if (!hasTile) {
                mAvailableTiles.add(tile)
            }
        }
    }

    fun addTile(tile: QSTile) {
        mTiles.add(tile)
        notifyDataSetChanged()

        setOrder(mTiles)
        refreshAvailableTiles()
    }

    fun setOrder(tiles: ArrayList<QSDragAdapter.QSTile>) {
        val keys = tiles.map { it.key }

        val tileString = TextUtils.join(",", keys)

        SettingsUtils.writeSecure(mContext, "sysui_qs_tiles", tileString)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QSViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qs_tile_layout, parent, false)
        return QSViewHolder(view)
    }

    override fun onBindViewHolder(holder: QSViewHolder, position: Int) {
        holder.setTitle(mTiles[holder.adapterPosition].title)
        holder.setIcon(mTiles[holder.adapterPosition].icon)
        holder.setCloseListener(View.OnClickListener {
            AlertDialog.Builder(mContext)
                    .setTitle(R.string.removing_tile)
                    .setMessage(String.format(holder.context.resources.getString(R.string.remove_tile), mTiles[holder.adapterPosition].title))
                    .setPositiveButton(R.string.yes, { _, _ ->
                        mTiles.removeAt(holder.adapterPosition)
                        setOrder(mTiles)
                        notifyItemRemoved(holder.adapterPosition)
                    })
                    .setNegativeButton(R.string.no, null)
                    .show()
        })
    }

    override fun getItemCount(): Int {
        return mTiles.size
    }

    class QSViewHolder(private var mView: View) : RecyclerView.ViewHolder(mView) {

        val context: Context
            get() = mView.context

        init {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mView.findViewById<View>(R.id.close_button).visibility = View.GONE
            }
        }

        fun setTitle(title: String) {
            val textView = mView.findViewById<TextView>(R.id.textView)
            textView.text = title
        }

        fun setIcon(icon: Drawable) {
            val imageView = mView.findViewById<ImageView>(R.id.imageView)
            imageView.setImageDrawable(icon)
        }

        fun setCloseListener(listener: View.OnClickListener) {
            mView.findViewById<View>(R.id.close_button).setOnClickListener(listener)
        }
    }

    class QSTile(var key: String, context: Context) {
        var title: String
        var icon: Drawable

        private var mParser: TileParser = TileParser(key, context)

        init {

            this.title = mParser.title
            this.icon = mParser.icon
        }
    }

    class TileParser(var key: String, private val mContext: Context) {
        lateinit var icon: Drawable
        lateinit var title: String

        init {

            parseKey()
        }

        private fun parseKey() {
            when {
                key.toLowerCase().contains("intent(") -> parseIntent()
                key.toLowerCase().contains("custom(") -> parseCustom()
                else -> parseStandard()
            }
        }

        private fun parseIntent() {
            val drawable = mContext.resources.getDrawable(R.drawable.ic_android_black_24dp, null)
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            icon = drawable

            val p = Pattern.compile("\\((.*?)\\)")
            val m = p.matcher(key)

            var title = ""

            while (!m.hitEnd()) {
                if (m.find()) title = m.group()
            }

            this.title = title.replace("(", "").replace(")", "")
        }

        private fun parseCustom() {
            val p = Pattern.compile("\\((.*?)\\)")
            val m = p.matcher(key)

            var name = ""

            while (!m.hitEnd()) {
                if (m.find()) name = m.group()
            }

            name = name.replace("(", "").replace(")", "")

            val packageName = name.split("/")[0]
            val component = name.split("/")[1]

            try {
                icon = mContext.packageManager.getApplicationIcon(packageName)
            } catch (e: Exception) {
            }

            try {
                val split = component.split(".")
                title = split[split.size - 1]
            } catch (e: Exception) {
            }

        }

        private fun parseStandard() {
            title = key.toLowerCase()

            var iconRes = R.drawable.ic_android_black_24dp

            when (key.toLowerCase()) {
                "wifi" -> iconRes = R.drawable.ic_signal_wifi_4_bar_black_24dp
                "bluetooth", "bt" -> iconRes = R.drawable.ic_bluetooth_black_24dp
                "color_inversion", "inversion" -> iconRes = R.drawable.ic_invert_colors_black_24dp
                "cell" -> iconRes = R.drawable.ic_signal_cellular_4_bar_black_24dp
                "do_not_disturb", "dnd" -> iconRes = R.drawable.ic_do_not_disturb_on_black_24dp
                "airplane" -> iconRes = R.drawable.ic_airplanemode_active_black_24dp
                "cast" -> iconRes = R.drawable.ic_cast_black_24dp
                "location" -> iconRes = R.drawable.ic_location_on_black_24dp
                "rotation" -> iconRes = R.drawable.ic_screen_rotation_black_24dp
                "flashlight" -> iconRes = R.drawable.ic_highlight_black_24dp
                "hotspot" -> iconRes = R.drawable.ic_wifi_tethering_black_24dp
                "battery" -> iconRes = R.drawable.ic_battery_full_black_24dp
                "sound" -> iconRes = R.drawable.ic_volume_up_black_24dp
                "sync" -> iconRes = R.drawable.ic_sync_black_24dp
                "nfc" -> iconRes = R.drawable.ic_nfc_black_24dp
                "data" -> iconRes = R.drawable.ic_data_usage_black_24dp
            }

            val drawable = mContext.resources.getDrawable(iconRes, null).current.mutate()
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            icon = drawable
        }
    }
}
