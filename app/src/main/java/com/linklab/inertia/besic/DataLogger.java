package com.linklab.inertia.besic;

import android.annotation.*;
import android.content.*;
import android.preference.*;

@SuppressLint("Registered") // Suppresses the intent filter added in the manifest file
public class DataLogger extends PreferenceActivity
{
    private String FileName, Content, Subdirectory, Directory;        // Variable names for the file characters and contents.
    private SharedPreferences sharedPreferences;        // Gets the shared preference from the system.

    DataLogger()
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.Directory = sharedPreferences.getString("directory_key", "");
    }
}
