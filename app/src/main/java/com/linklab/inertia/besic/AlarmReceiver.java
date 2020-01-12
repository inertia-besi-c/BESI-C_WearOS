package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This is the class that distributes the broadcast receivers for the alarm managers of the system. If any alarm intent needs to fire,
 * this receiver would accept the intent and determine which actions should follow based on the logic.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    /**
     * This method runs as soon as an intent of the alarm is seen by the system
     * @param context is the application context for the system
     * @param intent is the intent that is to be fired by the alarm
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String KEY, followupValue;      // Sets up the string variable for the identifiers
        Intent surveyIntent;        // Sets the intents for the broadcast receiver

        KEY = intent.getStringExtra(context.getResources().getString(R.string.survey_alarm_key));        // Gets the key identifier from the resource file
        followupValue = context.getResources().getString(R.string.followup_identifier);        // Gets the key identifier from the resource file

        if (KEY.equalsIgnoreCase(followupValue))        // Checks if the value of the key is the same as the followup value
        {
            surveyIntent = new Intent (context, FollowupSurvey.class);        // Calls an intent for a new activity
            surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       // Adds a new task for the service to start the activity
            context.startActivity(surveyIntent);        // Starts the activity specified
        }
    }
}
