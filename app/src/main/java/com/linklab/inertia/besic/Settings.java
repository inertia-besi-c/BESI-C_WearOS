package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.annotation.*;
import android.os.*;
import android.preference.*;
import androidx.annotation.*;

import java.util.ArrayList;

/**
 * This class is responsible for setting up the settings for the application.
 * This calls on a specified settings xml file that creates the appropriate values and their defaults.
 */
@SuppressLint("ExportedPreferenceActivity")     // Suppresses the intent filter added in the manifest file
public class Settings extends PreferenceActivity
{
    /**
     * This is the classes create method that starts the settings activity
     * @param savedInstanceState is a parameter passed into the method
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Calls the super class

        this.getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettingFragment()).commit();      // Sets up the settings screen based on the appropriate xml file
    }

    /**
     * A new class created in the preferences that links to the made settings file
     */
    public static class MainSettingFragment extends PreferenceFragment
    {
        /**
         * This is the classes create method that creates the setting activity
         * @param savedInstanceState is a parameter passed into the method
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);     // Call to the superclass
            this.addPreferencesFromResource(R.xml.settings);     // Creates the preference resource from the specified xml file

            setUpSummaryValue(findPreference("user_info"));
            setUpSummaryValue(findPreference("device_info"));
            setUpSummaryValue(findPreference("directory_key"));
            setUpSummaryValue(findPreference("deployment_key"));
            setUpSummaryValue(findPreference("low_battery_alert"));
            setUpSummaryValue(findPreference("low_battery_buzz"));
            setUpSummaryValue(findPreference("battery_remind"));
            setUpSummaryValue(findPreference("battery_hour_alert_start"));
            setUpSummaryValue(findPreference("battery_minute_alert_start"));
            setUpSummaryValue(findPreference("battery_second_alert_start"));
            setUpSummaryValue(findPreference("battery_hour_alert_end"));
            setUpSummaryValue(findPreference("battery_minute_alert_end"));
            setUpSummaryValue(findPreference("battery_second_alert_end"));
            setUpSummaryValue(findPreference("haptic_level"));
            setUpSummaryValue(findPreference("activity_start"));
            setUpSummaryValue(findPreference("activity_remind"));
            setUpSummaryValue(findPreference("pain_remind_interval"));
            setUpSummaryValue(findPreference("pain_remind_max"));
            setUpSummaryValue(findPreference("pain_remind"));
            setUpSummaryValue(findPreference("followup_trigger"));
            setUpSummaryValue(findPreference("followup_remind_interval"));
            setUpSummaryValue(findPreference("followup_remind_max"));
            setUpSummaryValue(findPreference("followup_remind"));
            setUpSummaryValue(findPreference("eod_remind_interval"));
            setUpSummaryValue(findPreference("eod_remind_max"));
            setUpSummaryValue(findPreference("eod_remind"));
            setUpSummaryValue(findPreference("eod_automatic_start_hour"));
            setUpSummaryValue(findPreference("eod_automatic_start_minute"));
            setUpSummaryValue(findPreference("eod_automatic_start_second"));
            setUpSummaryValue(findPreference("endofday_manual_start_time"));
            setUpSummaryValue(findPreference("eod_manual_start_hour"));
            setUpSummaryValue(findPreference("eod_manual_start_minute"));
            setUpSummaryValue(findPreference("eod_manual_start_second"));
            setUpSummaryValue(findPreference("eod_manual_end_hour"));
            setUpSummaryValue(findPreference("eod_manual_end_minute"));
            setUpSummaryValue(findPreference("eod_manual_end_second"));
            setUpSummaryValue(findPreference("heartrate_duration"));
            setUpSummaryValue(findPreference("heartrate_interval"));
            setUpSummaryValue(findPreference("accelerometer_data_limit"));
            setUpSummaryValue(findPreference("estimote_duration"));
            setUpSummaryValue(findPreference("estimote_interval"));
            setUpSummaryValue(findPreference("estimote_maximum_activity"));
        }
    }

    private static void setUpSummaryValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(preferenceChangeListener);
        preferenceChangeListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference)
            {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if (preference instanceof  EditTextPreference)
            {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
}
