package com.linklab.inertia.besic;

import android.annotation.SuppressLint;
import android.os.*;
import android.preference.*;
import androidx.annotation.*;

@SuppressLint("ExportedPreferenceActivity")
public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingFragment()).commit();
    }

    public static class MainSettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
