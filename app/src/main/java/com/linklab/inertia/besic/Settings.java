package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.annotation.*;
import android.os.*;
import android.preference.*;
import androidx.annotation.*;

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
        }
    }
}
