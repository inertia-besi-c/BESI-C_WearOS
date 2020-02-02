package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.annotation.SuppressLint;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.os.Bundle;

import androidx.annotation.Nullable;

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
    @Override       // Overrides the built in method
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
        @Override       // Overrides the built in method
        public void onCreate(@Nullable Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);     // Call to the superclass

            this.addPreferencesFromResource(R.xml.settings);     // Creates the preference resource from the specified xml file

            setUpSummaryValue(findPreference("user_info"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("device_info"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("directory_key"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("deployment_key"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("low_battery_alert"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("low_battery_buzz"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_remind"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_hour_alert_start"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_minute_alert_start"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_second_alert_start"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_hour_alert_end"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_minute_alert_end"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_second_alert_end"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("haptic_level"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("activity_start"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("activity_remind"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("pain_remind_interval"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("pain_remind_max"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("pain_remind"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("followup_trigger"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("followup_remind_interval"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("followup_remind_max"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("followup_remind"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_remind_interval"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_remind_max"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_remind"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_automatic_start_hour"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_automatic_start_minute"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_automatic_start_second"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("endofday_manual_start_time"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_start_hour"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_start_minute"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_start_second"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_end_hour"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_end_minute"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("eod_manual_end_second"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("heartrate_duration"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("heartrate_interval"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("accelerometer_data_limit"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("estimote_duration"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("estimote_interval"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("estimote_maximum_activity"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("sleepmode_setup"));     // Key of an item in the settings screen
            setUpSummaryValue(findPreference("battery_remind"));    // Key of an item in the settings screen
        }
    }

    /**
     * This method updates the items in the shared preferences if it is changed on the device.
     * @param preference is the preference item needed to be updated
     */
    private static void setUpSummaryValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(preferenceChangeListener);     // Sets a change listener on the preference item
        preferenceChangeListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));     // Updates the item
    }

    /**
     * A private class for the preference items that changes the value on the screen if changed from the default
     */
    private static Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener()
    {
        /**
         * A method that is called if there is a change to the preferences
         * @param preference is the preference item changes
         * @param newValue is the value that it is updated to
         * @return true if the process was performed
         */
        @Override       // Overrides the built in method
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {
            String stringValue = newValue.toString();       // Makes a new string with the value input
            if (preference instanceof ListPreference)       // Checks to see if the preference item is an instance of the object type
            {
                ListPreference listPreference = (ListPreference) preference;        // Makes a preference item
                int index = listPreference.findIndexOfValue(stringValue);       // Finds the index of the new choice
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);      // Makes the summary of the preference to be the new value
            }
            else if (preference instanceof  EditTextPreference)       // Checks to see if the preference item is an instance of the object type
            {
                preference.setSummary(stringValue);     // Sets the summary of the item to be the new value
            }
            return true;        // Returns true
        }
    };
}
