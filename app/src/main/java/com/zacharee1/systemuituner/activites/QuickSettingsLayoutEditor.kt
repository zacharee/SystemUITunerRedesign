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
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.QSDragAdapter
import com.zacharee1.systemuituner.util.pxToDp
import java.util.*

class QuickSettingsLayoutEditor : BaseAnimActivity() {
    private var mObserver: ContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_recycler)
        title = resources.getString(R.string.quick_settings)

        findViewById<View>(R.id.root).setBackgroundColor(Color.parseColor("#ff303030"))

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.content_main)
        val adapter = QSDragAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return ItemTouchHelper.Callback.makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Collections.swap(adapter.mTiles, viewHolder.adapterPosition, target.adapterPosition)
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

                adapter.setOrder(adapter.mTiles)
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

        mObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
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

        contentResolver.registerContentObserver(Settings.Secure.CONTENT_URI, true, mObserver!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item?.let {
            when (it.itemId) {
                android.R.id.home -> finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        contentResolver.unregisterContentObserver(mObserver!!)
    }

    private class AddTileView(context: Context, private val mDragAdapter: QSDragAdapter) : AlertDialog(context) {
        private val availableTiles: ArrayList<QSDragAdapter.QSTile>
        @SuppressLint("InflateParams")
        private val view: View = LayoutInflater.from(context).inflate(R.layout.activity_blank_recycler, null, false)

        init {
            view.setPadding(0, context.pxToDp(16f).toInt(), 0, context.pxToDp(16f).toInt())

            setView(view)

            availableTiles = ArrayList(mDragAdapter.mAvailableTiles)
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
                holder.mView.setOnClickListener {
                    if (!availableTiles[holder.adapterPosition].key.contains("intent(")) {
                        addTile(holder, true)
                    } else {
                        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null, false)
                        AlertDialog.Builder(context)
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
                mDragAdapter.addTile(availableTiles[holder.adapterPosition])
                if (removeFromAvail) mDragAdapter.mAvailableTiles.remove(availableTiles[holder.adapterPosition])
                dismiss()
            }

            override fun getItemCount(): Int {
                return availableTiles.size
            }

            inner class AddVH(internal var mView: View) : RecyclerView.ViewHolder(mView) {

                fun setText(text: String) {
                    val textView = mView.findViewById<TextView>(R.id.textView)
                    textView.text = text
                }
            }
        }
    }
}

