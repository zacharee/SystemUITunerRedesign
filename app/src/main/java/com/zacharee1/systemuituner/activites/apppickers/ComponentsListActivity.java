package com.zacharee1.systemuituner.activites.apppickers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
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

import java.util.ArrayList;
import java.util.TreeMap;

public class ComponentsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) finish();

        Bundle extras = intent.getExtras();
        if (extras == null) finish();

        String packageName = extras.getString("package");
        String appName = extras.getString("name");
        boolean isLeft = extras.getBoolean("isLeft");

        setTitle(appName);

        RecyclerView recyclerView = findViewById(R.id.app_rec);
        recyclerView.setAdapter(new CustomAdapter(getComponentInfo(packageName), this, isLeft));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<AppInfo> getComponentInfo(String packageName) {
        TreeMap<String, AppInfo> apps = new TreeMap<>();

        PackageManager pm = getPackageManager();

        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            for (ActivityInfo activity : info.activities) {
                apps.put(activity.name,
                        new AppInfo(activity.name,
                                activity.packageName,
                                activity.name,
                                activity.loadIcon(getPackageManager()))
                );
            }
        } catch (Exception e) {}

        return new ArrayList<>(apps.values());
    }
}
