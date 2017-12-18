package com.zacharee1.systemuituner.activites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.QSDragAdapter;
import com.zacharee1.systemuituner.util.SettingsUtils;
import com.zacharee1.systemuituner.util.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class QuickSettingsLayoutEditor extends AppCompatActivity {
    private ContentObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_recycler);

        findViewById(R.id.root).setBackgroundColor(Color.parseColor("#ff303030"));

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.content_main);
        final QSDragAdapter adapter = new QSDragAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(adapter.mTiles, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                adapter.setOrder(adapter.mTiles);
                return true;
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton addTile = new FloatingActionButton(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        addTile.setLayoutParams(params);
        addTile.setImageResource(R.drawable.ic_add_black_24dp);
        addTile.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        addTile.setUseCompatPadding(true);

        addTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddTileView(QuickSettingsLayoutEditor.this, adapter).show();
            }
        });

        FrameLayout root = findViewById(R.id.root);
        root.addView(addTile);

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (uri.equals(Settings.Secure.getUriFor("sysui_qs_tiles"))) {
                    if (!recyclerView.isAnimating()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.parseTileList();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        };

        getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getContentResolver().unregisterContentObserver(mObserver);
    }

    private static class AddTileView extends AlertDialog {
        private ArrayList<QSDragAdapter.QSTile> mAvailableTiles;
        private QSDragAdapter mDragAdapter;
        private View mView;

        public AddTileView(Context context, QSDragAdapter adapter) {
            super(context);
            mView = LayoutInflater.from(context).inflate(R.layout.activity_blank_recycler, null, false);
            mView.setPadding(0, (int)Utils.pxToDp(context, 16), 0, (int)Utils.pxToDp(context, 16));

            setView(mView);

            mAvailableTiles = new ArrayList<>(adapter.mAvailableTiles);
            QSDragAdapter.QSTile intent = new QSDragAdapter.QSTile("intent()", context);
            intent.title = getContext().getResources().getString(R.string.intent);
            mAvailableTiles.add(intent);
            mDragAdapter = adapter;
            setUpRecView();
        }

        private void setUpRecView() {
            RecyclerView recyclerView = mView.findViewById(R.id.content_main);
            recyclerView.setAdapter(new AddAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        private class AddAdapter extends RecyclerView.Adapter<AddAdapter.AddVH> {
            @Override
            public AddVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_tile_layout, parent, false);
                return new AddVH(view);
            }

            @Override
            public void onBindViewHolder(final AddVH holder, int position) {
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (!mAvailableTiles.get(holder.getAdapterPosition()).key.contains("intent(")) {
                            addTile(holder, true);
                        } else {
                            final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_input, null, false);
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.intent)
                                    .setView(dialogView)
                                    .setPositiveButton(R.string.ok, new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            EditText editText = dialogView.findViewById(R.id.editText);
                                            mAvailableTiles.set(holder.getAdapterPosition(), new QSDragAdapter.QSTile("intent(" + editText.getText().toString() + ")", getContext()));
                                            addTile(holder, false);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                        }
                    }
                });
                holder.setText(mAvailableTiles.get(holder.getAdapterPosition()).title);
            }

            private void addTile(AddVH holder, boolean removeFromAvail) {
                mDragAdapter.addTile(mAvailableTiles.get(holder.getAdapterPosition()));
                if (removeFromAvail) mDragAdapter.mAvailableTiles.remove(mAvailableTiles.get(holder.getAdapterPosition()));
                dismiss();
            }

            @Override
            public int getItemCount() {
                return mAvailableTiles.size();
            }

            public class AddVH extends RecyclerView.ViewHolder {
                View mView;

                public AddVH(View view) {
                    super(view);
                    mView = view;
                }

                public void setText(String text) {
                    TextView textView = mView.findViewById(R.id.textView);
                    textView.setText(text);
                }
            }
        }
    }
}

