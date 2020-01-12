package com.linklab.inertia.besic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FollowupSurveyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String identity = intent.getStringExtra("Alarm Type");

        if (identity.equalsIgnoreCase("Followup Survey"))
        {
            Intent surveyIntent = new Intent (context, FollowupSurvey.class);        // Calls an intent to start a new activity
            surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       // Adds a new task for the service to start the activity
            context.startActivity(surveyIntent);        // Starts the activity specified
        }
    }
}
