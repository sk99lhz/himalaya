package com.lhz.sk.himalaya.activitys;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.utils.AppTool;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initNavBar(true, "关于", false);
    }


    public static class AboutFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private Preference mVersion;
        private Preference mShare;
        private Preference mStar;
        private Preference mGithub;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference_about);
            mVersion = findPreference("version");
            mShare = findPreference("share");
            mStar = findPreference("star");
            mGithub = findPreference("github");
            mVersion.setSummary("v " + AppTool.getVersionName());
            setListener();
        }

        private void setListener() {
            mShare.setOnPreferenceClickListener(this);
            mStar.setOnPreferenceClickListener(this);
            mGithub.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mShare) {
                share();
                return true;
            } else if (preference == mStar) {
                openUrl(getString(R.string.about_project_url));
                return true;
            } else if (preference == mGithub) {
                openUrl(preference.getSummary().toString());
                return true;
            }
            return false;
        }

        @SuppressLint("StringFormatInvalid")
        private void share() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app, getString(R.string.app_name)));
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }

        private void openUrl(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

    }
}