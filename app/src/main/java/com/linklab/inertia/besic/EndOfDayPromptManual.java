package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

/**
 * This class is run only when the system is almost out of battery which is a defined variable set up in the
 * preference file. The user has the option to dismiss this warning or charge the watch.
 */
public class EndOfDayPromptManual extends WearableActivity
{
    private SystemInformation systemInformation;        // Private link to the class
    private DataLogger dataLogger;      // Private initialization to the data logger
    private Button ok;     // Initializes a button
    private String data;        // Sets up a string for data
    private TextView textView;      // Text view for the system
    private Intent EMA;      // Sets up the intents for this class
    private SharedPreferences sharedPreferences;        // Gets the shared preferences
    private Vibrator vibrator;      // Sets up the vibrator
    private Window window;      // Gets access to the window
    private int hapticLevel, activityStart;        // Sets up the haptic level

    /**
     * This is the created method that is run when the class is called
     * @param savedInstanceState is the instance state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is run on creation
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preferences from the shared preference object.
        this.systemInformation = new SystemInformation();       // Binds the variable to the calls in the class
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Sets the vibrator service.

        this.activityStart = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("activity_start", ""))) * 1000;       // Sets up the vibration level of the system for haptic feedback
        this.hapticLevel = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("haptic_level", "")));       // Sets up the vibration level of the system for haptic feedback

        this.unlockScreen();     // Unlocks the screen

        super.onCreate(savedInstanceState);     // Makes the screen and saves the instance
        this.setContentView(R.layout.activity_manual_prompt);      // Sets the view to show the low battery screen

        this.vibrator.vibrate(this.activityStart);      // Vibrates the system
        this.ok = findViewById(R.id.ok);        // Sets the dismiss button
        this.textView = findViewById(R.id.request2);     // Sets the text view
        this.textView.setText(getString(R.string.endofday_request));      // Message to be shown on the screen

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Manual Prompt" + "," + "Starting End of Day EMA Manual Prompt";      // Sets up the data to be logged
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");        // Saves the data

        this.ok.setOnClickListener(new View.OnClickListener() // Waits for the dismiss button to be clicked.
        {
            /**
             * This is run when the button is clicked
             * @param v is the view that is needed to see the button
             */
            @Override
            public void onClick(View v)         // When the button is clicked
            {
                vibrator.vibrate(hapticLevel);       // Haptic feedback for the dismiss button

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Manual Prompt" + "," + "Starting End of Day EMA";      // Sets up the data to be logged
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");        // Saves the data

                EMA = new Intent(getApplicationContext(), EndOfDaySurvey.class);       // Makes an intent to the estimote class
                startActivity(EMA);       // Calls to start the activity

                finish();       // It stops the class and the buzzing
            }
        });

        this.setAmbientEnabled();            // Enables Always-on
    }

    /**
     * This method sets up the screen actions that go along with waking up to the activity and how the screen behaves while the activity is ongoing
     */
    private void unlockScreen()
    {
        this.window = this.getWindow();     // Gets access to the screen of the device
        this.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      // Makes sure the device can wake up if locked
        this.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);        // Makes sure the screen is on if off
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        // Makes sure the screen stays on for the duration of the activity
    }

    /**
     * This method is called when the activity needs to quit
     */
    @Override
    public void onDestroy()     // This is called when the activity is destroyed.
    {
        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Manual Prompt" + "," + "Killing End of day EMA Class";      // Sets up the data to be logged     // Sets data to be logged by system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");        // Saves the data

        super.onDestroy();      // The activity is killed.
    }
}
