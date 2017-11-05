package com.zacharee1.systemuituner.activites;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.zacharee1.systemuituner.LauncherActivity;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.RecreateHandler;
import com.zacharee1.systemuituner.misc.SettingsUtils;
import com.zacharee1.systemuituner.misc.Utils;

import java.util.Arrays;

@SuppressWarnings("unused")
public class SetupActivity extends AppIntro2
{
    @SuppressWarnings("FieldCanBeLocal")
    private String[] permissionsNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backButton.setVisibility(View.GONE);
        skipButton.setVisibility(View.GONE);
        showPagerIndicator(false);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        if (intent != null) {
            permissionsNeeded = intent.getStringArrayExtra("permission_needed");

            addSlide(PermsFragment.newInstance(
                    getResources().getString(R.string.permissions),
                    getResources().getString(R.string.adb_setup),
                    permissionsNeeded,
                    getResources().getColor(R.color.intro_1, null)
            ));
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        String[] missing = Utils.checkPermissions(this, permissionsNeeded);

        if (missing.length > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.missing_perms)
                    .setMessage(Arrays.toString(missing))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        } else {
            Utils.startUp(this);
            finish();
        }
    }

    public void launchInstructions(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://zacharywander.tk/#sysuituner_adb")));
    }

    public static class PermsFragment extends Fragment {
        private View mView;

        public static PermsFragment newInstance(String title, String description, String[] permissions, int color) {
            PermsFragment fragment = new PermsFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("description", description);
            args.putInt("color", color);
            args.putStringArray("permissions", permissions);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
        {
            Bundle args = getArguments();

            mView = inflater.inflate(R.layout.permissions_fragment, container, false);

            mView.setBackgroundColor(args.getInt("color"));
            mView.findViewById(R.id.adb_instructions).setBackgroundTintList(ColorStateList.valueOf(args.getInt("color")));

            TextView title = mView.findViewById(R.id.title);
            title.setText(args.getString("title", ""));

            TextView desc = mView.findViewById(R.id.description);
            desc.setText(args.getString("description", ""));

            LinearLayout text = mView.findViewById(R.id.perms_layout);
            String[] perms = args.getStringArray("permissions");

            if (perms != null) {
                for (String perm : perms) {
                    String command = "adb shell pm grant " + getActivity().getPackageName() + " ";

                    TextView textView = new TextView(getActivity());
                    textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    textView.setTextIsSelectable(true);
                    textView.setText(command.concat(perm));
                    textView.setPadding(
                            0,
                            (int)Utils.pxToDp(getActivity(), 8),
                            0,
                            0
                    );

                    text.addView(textView);
                }
            }

            return mView;
        }
    }
}