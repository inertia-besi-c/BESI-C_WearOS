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
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is run when the second automatic end of day ema is called to run.
 * This has the ability to either start the survey, or dismiss the ema completely.
 */
public class EndOfDayPrompt2 extends WearableActivity
{
    private SystemInformation systemInformation;        // Private link to the class
    private DataLogger dataLogger;      // Private initialization to the data logger
    private Button proceed, dismiss;     // Initializes a button
    private String data;        // Sets up a string for data
    private TextView textView;      // Text view for the system
    private Timer reminderTimer;       // Sets up the timer for the system
    private Intent endOfDay;      // Sets up the intents for this class
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
        this.setContentView(R.layout.activity_prompt);      // Sets the view to show the low battery screen

        this.vibrator.vibrate(this.activityStart);      // Vibrates the system
        this.proceed = findViewById(R.id.proceed);        // Sets the proceeding button
        final Button snooze = findViewById(R.id.snooze);        // Sets the snooze button
        this.dismiss = findViewById(R.id.dismiss);        // Sets the dismiss button

        this.dismiss.setVisibility(View.INVISIBLE);     // Removes the dismiss button
        snooze.setText(getResources().getString(R.string.dismiss_button));     // Sets the string to be that of the snooze button
        snooze.setBackgroundColor(getResources().getColor(R.color.dark_red));      // Sets the color of the snooze button

        this.textView = findViewById(R.id.request);     // Sets the text view
        this.textView.setText(getString(R.string.endofday_request));      // Message to be shown on the screen

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Prompt 2" + "," + "Starting End of Day EMA prompt 2 Class";      // Sets up the data to be logged
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");        // Saves the data

        this.reminderTimer = new Timer();       // Sets up the reminderTimer
        this.reminderTimer.schedule(new TimerTask()         // Schedules the reminderTimer at a fixed rate
        {
            /**
             * The following is called to run
             */
            @Override
            public void run()
            {
                vibrator.vibrate(hapticLevel);       // Haptic feedback for the dismiss button

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Prompt 2" + "," + "Automatically dismissing EMA prompt";      // Sets up the data to be logged
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");        // Saves the data

                snooze.performClick();     // Automatically snoozes the question
            }
        }, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_remind_interval", ""))) * 1000);      // Repeats at the specified interval

        this.proceed.setOnClickListener(new View.OnClickListener()      // Waits for the button to be clicked.
        {
            /**
             * This is run when the button is clicked
             * @param v is the view that is needed to see the button
             */
            @Override
            public void onClick(View v)         // When the button is clicked
            {
                vibrator.vibrate(hapticLevel);       // Haptic feedback for the dismiss button

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Prompt 2" + "," + "Proceed button clicked";      // Sets up the data to be logged
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");        // Saves the data

                endOfDay = new Intent(getApplicationContext(), EndOfDaySurvey.class);       // Makes an intent to the estimote class
                startActivity(endOfDay);       // Calls to start the activity

                finish();       // It stops the class and the buzzing
            }
        });

        snooze.setOnClickListener(new View.OnClickListener() // Waits for the button to be clicked.
        {
            /**
             * This is run when the button is clicked
             * @param v is the view that is needed to see the button
             */
            @Override
            public void onClick(View v)         // When the button is clicked
            {
                vibrator.vibrate(hapticLevel);       // Haptic feedback for the dismiss button

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Prompt 2" + "," + "Snooze button clicked";      // Sets up the data to be logged
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");        // Saves the data

                systemInformation.toast(getApplicationContext(), getResources().getString(R.string.thank_you));     // Gets the string and calls a toast on it

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
        this.reminderTimer.cancel();        // Cancels the timer

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "End of Day Prompt 2" + "," + "Cleaning up the Class";        // Sets data to be logged by system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");        // Saves the data

        super.onDestroy();      // The activity is killed.
    }
}
