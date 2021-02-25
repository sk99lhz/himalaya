package com.lhz.sk.himalaya.activitys;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import androidx.preference.Preference;

import androidx.preference.PreferenceFragmentCompat;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.utils.AppTool;
import com.lhz.sk.himalaya.utils.ToastUtils;


public class SettingActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initNavBar(true, "设置", false);

    }


    public static class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mSoundEffect;
        private Preference mHelp;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference_setting);

            mSoundEffect = findPreference(getString(R.string.setting_key_sound_effect));
            mHelp = findPreference(getString(R.string.setting_key_help_app));
            mSoundEffect.setOnPreferenceClickListener(this);
            mHelp.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mSoundEffect) {
                startEqualizer();
                return true;
            } else if (preference == mHelp) {
                help();
                return true;
            }
            return false;
        }

        private void help() {
            ToastUtils.showToast(getActivity(), "测试中");
        }

        private void startEqualizer() {
            if (AppTool.isAudioControlPanelAvailable(getActivity())) {
                Intent intent = new Intent();
                String packageName = getActivity().getPackageName();
                intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, new MediaPlayer().getAudioSessionId());

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();

                }
            } else {
                ToastUtils.showToast(getActivity(), "设备不支持");

                // Toast.makeText(getActivity(), "设备不支持", Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return false;
        }
    }
}