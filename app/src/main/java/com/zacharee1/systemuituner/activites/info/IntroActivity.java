package com.zacharee1.systemuituner.activites.info;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.instructions.SetupActivity;
import com.zacharee1.systemuituner.util.SuUtils;
import com.zacharee1.systemuituner.util.Utils;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        backButton.setVisibility(View.GONE);
        skipButton.setVisibility(View.VISIBLE);

        addSlide(SlideFragment.newInstance(
                getResources().getString(R.string.welcome),
                getResources().getString(R.string.intro_1),
                0,
                getResources().getColor(R.color.intro_1, null)
        ));

        addSlide(SlideFragment.newInstance(
                getResources().getString(R.string.some_things),
                getResources().getString(R.string.intro_2),
                0,
                getResources().getColor(R.color.intro_2, null)
        ));

        addSlide(SlideFragment.newInstance(
                getResources().getString(R.string.auto_detect),
                getResources().getString(R.string.intro_3),
                0,
                getResources().getColor(R.color.intro_3, null)
        ));

        addSlide(SlideFragment.newInstance(
                getResources().getString(R.string.no_root),
                getResources().getString(R.string.intro_4),
                0,
                getResources().getColor(R.color.intro_4, null)
        ));

        addSlide(SlideFragment.newInstance(
                getResources().getString(R.string.permissions),
                getResources().getString(R.string.intro_5),
                0,
                getResources().getColor(R.color.intro_5, null)
        ));

        setColorTransitionsEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        int index = getSlides().indexOf(newFragment);

        if (index > 0) {
            skipButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
        } else {
            skipButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        String[] perms = new String[] {
                Manifest.permission.WRITE_SECURE_SETTINGS,
                Manifest.permission.DUMP,
                Manifest.permission.PACKAGE_USAGE_STATS
        };

        String[] ret;

        if ((ret = Utils.checkPermissions(this, perms)).length > 0) {
            if (SuUtils.testSudo()) {
                SuUtils.sudo("pm grant com.zacharee1.systemuituner android.permission.WRITE_SECURE_SETTINGS ; " +
                        "pm grant com.zacharee1.systemuituner android.permission.DUMP ; " +
                        "pm grant com.zacharee1.systemuituner android.permission.PACKAGE_USAGE_STATS");
                Utils.startUp(this);
                finishAndSave();
            } else {
                Intent intent = new Intent(this, SetupActivity.class);
                intent.putExtra("permission_needed", ret);
                startActivity(intent);
                finishAndSave();
            }
        } else {
            Utils.startUp(this);
            finishAndSave();
        }
    }

    private void finishAndSave() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("show_intro", false).apply();

        finish();
    }

    public static class SlideFragment extends Fragment implements ISlideBackgroundColorHolder {
        private View mView;

        public static SlideFragment newInstance(String title, String description, int drawableId, int color) {
            SlideFragment fragment = new SlideFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("description", description);
            args.putInt("color", color);
            args.putInt("drawableId", drawableId);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
        {
            Bundle args = getArguments();

            mView = inflater.inflate(R.layout.fragment_intro2, container, false);

            TextView title = mView.findViewById(R.id.title);
            title.setText(args.getString("title", ""));

            TextView desc = mView.findViewById(R.id.description);
            desc.setText(args.getString("description", ""));

            ImageView drawable = mView.findViewById(R.id.image);
            int drawableId = args.getInt("drawableId");
            if (drawableId != 0) drawable.setImageResource(drawableId);

            return mView;
        }

        @Override
        public void setBackgroundColor(int backgroundColor) {
            mView.setBackgroundColor(backgroundColor);
        }

        @Override
        public int getDefaultBackgroundColor() {
            return getArguments().getInt("color");
        }
    }
}
