package com.zacharee1.systemuituner.instructions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.Utils;

import org.w3c.dom.Text;
import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.annotation.Dimension.SP;

public class InstructionsActivity extends AppIntro2 {
    public static final String ARG_TITLE = "title";
    public static final String ARG_DESC = "desc";
    public static final String ARG_LAYOUT = "layout";
    public static final String ARG_BG_COLOR = "bg_color";
    public static final String ARG_TITLE_COLOR = "title_color";
    public static final String ARG_DESC_COLOR = "desc_color";
    public static final String ARG_COMMANDS = "commands";

    private static final String WINDOWS = "windows";
    private static final String MAC = "mac";
    private static final String UBUNTU = "ubuntu";
    private static final String LINUX = "linux";
    private static final String FEDORA = "fedora";

    private static final String PREFIX = "adb shell pm grant com.zacharee1.systemuituner ";

    private Instructions mSelector;
    private Instructions mInitial;
    private Instructions mWindows;
    private Instructions mMac;
    private Instructions mUbuntu;
    private Instructions mLinux;
    private Instructions mFedora;
    private Commands mCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        mSelector = Instructions.newInstance(getResources().getString(R.string.choose_your_weapon),
                getResources().getString(R.string.which_os),
                R.layout.fragment_adb_select,
                getResources().getColor(R.color.intro_5, null));

        addSlide(mSelector);

        mInitial = Instructions.newInstance(getResources().getString(R.string.initial_setup),
                getResources().getString(R.string.on_device),
                R.layout.fragment_adb_initial,
                getResources().getColor(R.color.intro_2, null));

        mWindows = Instructions.newInstance(getResources().getString(R.string.windows_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_windows,
                getResources().getColor(R.color.intro_1, null));

        mMac = Instructions.newInstance(getResources().getString(R.string.mac_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_mac,
                getResources().getColor(R.color.intro_1, null));

        mUbuntu = Instructions.newInstance(getResources().getString(R.string.ubuntu_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_ubuntu,
                getResources().getColor(R.color.intro_1, null));

        mLinux = Instructions.newInstance(getResources().getString(R.string.linux_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_linux,
                getResources().getColor(R.color.intro_1, null));

        mFedora = Instructions.newInstance(getResources().getString(R.string.fedora_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_fedora,
                getResources().getColor(R.color.intro_1, null));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ArrayList<String> cmds = extras.getStringArrayList(ARG_COMMANDS);

        mCommands = Commands.newInstance(getResources().getString(R.string.run_commands),
                getResources().getString(R.string.run_on_computer),
                getResources().getColor(R.color.intro_4, null),
                cmds);

        backButton.setVisibility(View.GONE);
        skipButtonEnabled = false;
        doneButton.setVisibility(View.GONE);
        showPagerIndicator(false);

        setColorTransitionsEnabled(true);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        int oldIndex = fragments.indexOf(oldFragment);
        int newIndex = fragments.indexOf(newFragment);

        if (newIndex == fragments.size() - 1 || newIndex == -1) {
            nextButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
        } else if (newIndex > 0) {
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
    }

    private void addProperSlides(String which) {
        removeAllExtraSlides();

        addSlide(mInitial);
        switch (which) {
            case WINDOWS:
                addSlide(mWindows);
                break;
            case MAC:
                addSlide(mMac);
                break;
            case UBUNTU:
                addSlide(mUbuntu);
                break;
            case LINUX:
                addSlide(mLinux);
                break;
            case FEDORA:
                addSlide(mFedora);
                break;
        }
        addSlide(mCommands);

        mPagerAdapter.notifyDataSetChanged();

        nextButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.GONE);
    }

    private void removeAllExtraSlides() {
        fragments.remove(mInitial);
        fragments.remove(mWindows);
        fragments.remove(mMac);
        fragments.remove(mUbuntu);
        fragments.remove(mLinux);
        fragments.remove(mFedora);
        fragments.remove(mCommands);
        mPagerAdapter.notifyDataSetChanged();
    }

    public void chooseWindows(View v) {
        addProperSlides(WINDOWS);
    }

    public void chooseMac(View v) {
        addProperSlides(MAC);
    }

    public void chooseUbuntu(View v) {
        addProperSlides(UBUNTU);
    }

    public void chooseLinux(View v) {
        addProperSlides(LINUX);
    }

    public void chooseFedora(View v) {
        addProperSlides(FEDORA);
    }

    public static class Instructions extends Fragment implements ISlideBackgroundColorHolder {
        protected View mView;

        public static Instructions newInstance(CharSequence title, CharSequence description,
                                           @LayoutRes int layoutId,
                                           @ColorInt int bgColor) {
            return newInstance(title, description, layoutId, bgColor, 0, 0);
        }

        public static Instructions newInstance(CharSequence title, CharSequence description,
                                           @LayoutRes int layoutId, @ColorInt int bgColor,
                                           @ColorInt int titleColor, @ColorInt int descColor) {
            Instructions slide = new Instructions();
            Bundle args = new Bundle();
            args.putString(ARG_TITLE, title.toString());
            args.putString(ARG_DESC, description.toString());
            args.putInt(ARG_LAYOUT, layoutId);
            args.putInt(ARG_BG_COLOR, bgColor);
            args.putInt(ARG_TITLE_COLOR, titleColor);
            args.putInt(ARG_DESC_COLOR, descColor);
            slide.setArguments(args);

            return slide;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            mView = inflater.inflate(getLayoutId(), container, false);

            View main = mView.findViewById(R.id.main);
            TextView title = mView.findViewById(R.id.title);
            TextView desc = mView.findViewById(R.id.description);
            LinearLayout holder = mView.findViewById(R.id.custom_layout_holder);

            Bundle args = getArguments();

            main.setBackgroundColor(args.getInt(ARG_BG_COLOR));
            title.setText(args.getString(ARG_TITLE));
            desc.setText(args.getString(ARG_DESC));

            if (args.getInt(ARG_LAYOUT) != 0 && args.getInt(ARG_LAYOUT) != -1) {
                ViewGroup viewParent = (ViewGroup) View.inflate(getActivity(), args.getInt(ARG_LAYOUT), null);
                ViewGroup viewChild = (ViewGroup) viewParent.getChildAt(0);

                ViewGroup view = viewChild != null ? viewChild : viewParent;

                for (int i = 0; i < view.getChildCount(); i++) {
                    View v = view.getChildAt(i);

                    if (v instanceof TextView) {
                        ((TextView) v).setText(Html.fromHtml(((TextView) v).getText().toString()));
                    }
                }

                holder.addView(viewParent);
            }

            return mView;
        }

        public int getLayoutId() {
            return R.layout.fragment_intro_custom_center;
        }

        @Override
        public int getDefaultBackgroundColor() {
            return getArguments().getInt(ARG_BG_COLOR);
        }

        @Override
        public void setBackgroundColor(int backgroundColor) {
            mView.setBackgroundColor(backgroundColor);
        }
    }

    public static class Commands extends Instructions {
        public static Commands newInstance(CharSequence title, CharSequence description,
                                               @ColorInt int bgColor,
                                               ArrayList<String> commands) {
            return newInstance(title, description, bgColor, 0, 0, commands);
        }

        public static Commands newInstance(CharSequence title, CharSequence description,
                                               @ColorInt int bgColor,
                                               @ColorInt int titleColor, @ColorInt int descColor,
                                               ArrayList<String> commands) {
            Commands slide = new Commands();
            Bundle args = new Bundle();
            args.putString(ARG_TITLE, title.toString());
            args.putString(ARG_DESC, description.toString());
            args.putInt(ARG_BG_COLOR, bgColor);
            args.putInt(ARG_TITLE_COLOR, titleColor);
            args.putInt(ARG_DESC_COLOR, descColor);
            args.putStringArrayList(ARG_COMMANDS, commands);
            slide.setArguments(args);

            return slide;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            ArrayList<String> commands = getArguments().getStringArrayList(ARG_COMMANDS);
            LinearLayout holder = view.findViewById(R.id.custom_layout_holder);

            for (String command : commands) {
                TextView textView = new TextView(getActivity());
                textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setTextIsSelectable(true);
                textView.setText(PREFIX.concat(command));
                textView.setPadding(
                        0,
                        (int)Utils.pxToDp(getActivity(), 8),
                        0,
                        0
                );

                holder.addView(textView);
            }

            return view;
        }
    }
}
