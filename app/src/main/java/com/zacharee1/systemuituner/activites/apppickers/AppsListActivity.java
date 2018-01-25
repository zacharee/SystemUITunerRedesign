package com.zacharee1.systemuituner.activites.apppickers;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.AppInfo;
import com.zacharee1.systemuituner.misc.CustomAdapter;
import com.zacharee1.systemuituner.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AppsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        setTitle(R.string.select_app);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) finish();

        Bundle extras = intent.getExtras();
        if (extras == null) finish();

        final boolean isLeft = extras.getBoolean("isLeft");

        final RecyclerView recyclerView = findViewById(R.id.app_rec);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final CustomAdapter adapter = new CustomAdapter(getAppInfo(), AppsListActivity.this, isLeft);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                        findViewById(R.id.progress).setVisibility(View.GONE);
                    }
                });
            }
        }).start();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    private ArrayList<AppInfo> getAppInfo() {
        TreeMap<String, AppInfo> appMap = new TreeMap<>();

        final List<ApplicationInfo> apps = Utils.getInstalledApps(this);

        final ProgressBar bar = findViewById(R.id.progress);
        bar.setMax(apps.size());

        for (final ApplicationInfo info : apps) {
            try {
                if (getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.length > 1) {
                    appMap.put(info.loadLabel(getPackageManager()).toString(),
                            new AppInfo(info.loadLabel(getPackageManager()).toString(),
                                    info.packageName,
                                    null,
                                    info.loadIcon(getPackageManager()))
                    );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar.setProgress(apps.indexOf(info) + 1);
                        }
                    });
                }
            } catch (Exception e) {}
        }

        return new ArrayList<>(appMap.values());
    }
}
