package com.zacharee1.systemuituner.activites.instructions;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.util.Utils;

import java.util.ArrayList;

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
    private Commands mCommands;

    private Instructions mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Intro);
        super.onCreate(savedInstanceState);

        mInstructions = Instructions.newInstance(getResources().getString(R.string.windows_setup),
                getResources().getString(R.string.on_computer),
                R.layout.fragment_adb_windows,
                getResources().getColor(R.color.intro_1, null));

        mSelector = Instructions.newInstance(getResources().getString(R.string.choose_your_weapon),
                getResources().getString(R.string.which_os),
                R.layout.fragment_adb_select,
                getResources().getColor(R.color.intro_5, null));

        mInitial = Instructions.newInstance(getResources().getString(R.string.initial_setup),
                getResources().getString(R.string.on_device),
                R.layout.fragment_adb_initial,
                getResources().getColor(R.color.intro_2, null));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ArrayList<String> cmds = extras.getStringArrayList(ARG_COMMANDS);

        mCommands = Commands.newInstance(getResources().getString(R.string.run_commands),
                getResources().getString(R.string.run_on_computer),
                getResources().getColor(R.color.intro_4, null),
                cmds);

        addSlide(mSelector);
        addSlide(mInitial);
        addSlide(mInstructions);
        addSlide(mCommands);

        pager.setOffscreenPageLimit(300);

        backButton.setVisibility(View.GONE);
        skipButtonEnabled = false;
        doneButton.setVisibility(View.GONE);
//        showPagerIndicator(false);

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

    private void replaceContentById(@LayoutRes int layout) {
        mInstructions.setInternalLayout(layout);
    }

    private void setInstructionsTitle(String title) {
        mInstructions.setTitle(title);
    }

    public void chooseWindows(View v) {
        replaceContentById(R.layout.fragment_adb_windows);
        setInstructionsTitle(getResources().getString(R.string.windows_setup));
    }

    public void chooseMac(View v) {
        replaceContentById(R.layout.fragment_adb_mac);
        setInstructionsTitle(getResources().getString(R.string.mac_setup));
    }

    public void chooseUbuntu(View v) {
        replaceContentById(R.layout.fragment_adb_ubuntu);
        setInstructionsTitle(getResources().getString(R.string.ubuntu_setup));
    }

    public void chooseLinux(View v) {
        replaceContentById(R.layout.fragment_adb_linux);
        setInstructionsTitle(getResources().getString(R.string.linux_setup));
    }

    public void chooseFedora(View v) {
        replaceContentById(R.layout.fragment_adb_fedora);
        setInstructionsTitle(getResources().getString(R.string.fedora_setup));
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

            Log.e("onCreateView()", getArguments().getString(ARG_TITLE));

            View main = mView.findViewById(R.id.main);
            TextView title = mView.findViewById(R.id.title);
            TextView desc = mView.findViewById(R.id.description);

            Bundle args = getArguments();

            main.setBackgroundColor(args.getInt(ARG_BG_COLOR));
            title.setText(args.getString(ARG_TITLE));
            desc.setText(args.getString(ARG_DESC));

            if (args.getInt(ARG_LAYOUT) != 0 && args.getInt(ARG_LAYOUT) != -1) {
                setInternalLayout(args.getInt(ARG_LAYOUT));
            }

            return mView;
        }

        public <T extends View> T findViewById(@IdRes int id) {
            return mView.findViewById(id);
        }

        public int getLayoutId() {
            return R.layout.fragment_intro_custom_center;
        }

        public void setTitle(String title) {
            TextView textView = mView.findViewById(R.id.title);
            textView.setText(formatText(title));
        }

        public void setInternalLayout(@LayoutRes int layoutId) {
            LinearLayout holder = mView.findViewById(R.id.custom_layout_holder);
            holder.removeAllViews();

            ViewGroup viewParent = (ViewGroup) View.inflate(getActivity(), layoutId, null);
            ViewGroup viewChild = (ViewGroup) viewParent.getChildAt(0);

            ViewGroup view = viewChild != null ? viewChild : viewParent;

            for (int i = 0; i < view.getChildCount(); i++) {
                View v = view.getChildAt(i);

                if (v instanceof TextView) {
                    ((TextView) v).setText(formatText(((TextView) v).getText().toString()));
                    ((TextView) v).setLinksClickable(true);
                    ((TextView) v).setMovementMethod(LinkMovementMethod.getInstance());
                    ((TextView) v).setLinkTextColor(getResources().getColorStateList(R.color.white, null));
                    ((TextView) v).setTextColor(getResources().getColorStateList(R.color.white, null));
                }
            }

            holder.addView(viewParent);
        }

        @Override
        public int getDefaultBackgroundColor() {
            return getArguments().getInt(ARG_BG_COLOR);
        }

        @Override
        public void setBackgroundColor(int backgroundColor) {
            mView.setBackgroundColor(backgroundColor);
        }

        private Spanned formatText(String text) {
            return Html.fromHtml(text);
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
