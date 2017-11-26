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

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.AppInfo;
import com.zacharee1.systemuituner.misc.CustomAdapter;
import com.zacharee1.systemuituner.util.Utils;

import java.util.ArrayList;
import java.util.TreeMap;

public class AppsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) finish();

        Bundle extras = intent.getExtras();
        if (extras == null) finish();

        boolean isLeft = extras.getBoolean("isLeft");

        RecyclerView recyclerView = findViewById(R.id.app_rec);
        recyclerView.setAdapter(new CustomAdapter(getAppInfo(), this, isLeft));
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

        for (ApplicationInfo info : Utils.getInstalledApps(this)) {
            try {
                if (getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.length > 1) {
                    appMap.put(info.loadLabel(getPackageManager()).toString(),
                            new AppInfo(info.loadLabel(getPackageManager()).toString(),
                                    info.packageName,
                                    null,
                                    info.loadIcon(getPackageManager()))
                    );
                }
            } catch (Exception e) {}
        }

        return new ArrayList<>(appMap.values());
    }
}
