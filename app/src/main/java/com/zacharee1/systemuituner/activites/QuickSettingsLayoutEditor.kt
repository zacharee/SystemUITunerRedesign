package com.zacharee1.systemuituner.activites

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.QSDragAdapter
import com.zacharee1.systemuituner.util.pxToDp
import com.zacharee1.systemuituner.util.writeSecure
import kotlinx.android.synthetic.main.activity_blank_recycler.*
import java.util.*

class QuickSettingsLayoutEditor : BaseAnimActivity() {
    private var observer: ContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_recycler)
        title = resources.getString(R.string.quick_settings)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.content_main)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val marshmallowInfo = TextView(this)
            marshmallowInfo.text = resources.getText(R.string.qs_info_marshmallow)
            marshmallowInfo.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            marshmallowInfo.textSize = 24f
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    pxToDp(48f).toInt(), Gravity.TOP)

            root.addView(marshmallowInfo, params)

            val recParams = recyclerView.layoutParams as FrameLayout.LayoutParams
            recParams.topMargin = params.height
            recyclerView.layoutParams = recParams
        }

        val adapter = QSDragAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Collections.swap(adapter.tiles, viewHolder.adapterPosition, target.adapterPosition)
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

                adapter.setOrder(adapter.tiles)
                return true
            }
        }).attachToRecyclerView(recyclerView)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val addTile = FloatingActionButton(this)

            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.BOTTOM or Gravity.END
            addTile.layoutParams = params
            addTile.setImageResource(R.drawable.ic_add_black_24dp)
            addTile.imageTintList = ColorStateList.valueOf(Color.WHITE)
            addTile.useCompatPadding = true

            addTile.setOnClickListener { AddTileView(this@QuickSettingsLayoutEditor, adapter).show() }

            val root = findViewById<FrameLayout>(R.id.root)
            root.addView(addTile)
        }

        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                if (uri == Settings.Secure.getUriFor("sysui_qs_tiles")) {
                    if (!recyclerView.isAnimating) {
                        Handler(Looper.getMainLooper()).post {
                            adapter.parseTileList()
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        contentResolver.registerContentObserver(Settings.Secure.CONTENT_URI, true, observer!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_qs, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_reset -> {
                writeSecure("sysui_qs_tiles", null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        contentResolver.unregisterContentObserver(observer!!)
    }

    private class AddTileView(context: Context, private val dragAdapter: QSDragAdapter) : AlertDialog(context) {
        private val availableTiles: ArrayList<QSDragAdapter.QSTile>
        @SuppressLint("InflateParams")
        private val view: View = LayoutInflater.from(context).inflate(R.layout.activity_blank_recycler, null, false)

        init {
            view.setPadding(0, context.pxToDp(16f).toInt(), 0, context.pxToDp(16f).toInt())

            setView(view)

            availableTiles = ArrayList(dragAdapter.availableTiles)
            val intent = QSDragAdapter.QSTile("intent()", context)
            intent.title = getContext().resources.getString(R.string.intent)
            availableTiles.add(intent)
            setUpRecView()
        }

        private fun setUpRecView() {
            val recyclerView = view.findViewById<RecyclerView>(R.id.content_main)
            recyclerView.adapter = AddAdapter()
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        private inner class AddAdapter : RecyclerView.Adapter<AddAdapter.AddVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddVH {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.add_tile_layout, parent, false)
                return AddVH(view)
            }

            @SuppressLint("InflateParams")
            override fun onBindViewHolder(holder: AddVH, position: Int) {
                holder.itemView.setOnClickListener {
                    if (!availableTiles[holder.adapterPosition].key.contains("intent(")) {
                        addTile(holder, true)
                    } else {
                        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null, false)
                        MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.intent)
                                .setView(dialogView)
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    val editText = dialogView.findViewById<EditText>(R.id.editText)
                                    availableTiles[holder.adapterPosition] = QSDragAdapter.QSTile("intent(" + editText.text.toString() + ")", context)
                                    addTile(holder, false)
                                }
                                .setNegativeButton(R.string.cancel, null)
                                .show()
                    }
                }
                holder.setText(availableTiles[holder.adapterPosition].title)
            }

            private fun addTile(holder: AddVH, removeFromAvail: Boolean) {
                dragAdapter.addTile(availableTiles[holder.adapterPosition])
                if (removeFromAvail) dragAdapter.availableTiles.remove(availableTiles[holder.adapterPosition])
                dismiss()
            }

            override fun getItemCount(): Int {
                return availableTiles.size
            }

            private inner class AddVH(view: View) : RecyclerView.ViewHolder(view) {
                fun setText(text: String) {
                    val textView = itemView.findViewById<TextView>(R.id.textView)
                    textView.text = text
                }
            }
        }
    }
}

