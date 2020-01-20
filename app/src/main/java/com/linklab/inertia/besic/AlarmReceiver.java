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
        SystemInformation systemInformation;        // sets a system information variable
        DataLogger dataLogger, checkEODDate;      // Sets a datalogger variable
        String KEY, data, followupValue, endOfDayValue;      // Sets up the string variable for the identifiers
        Intent surveyIntent;        // Sets the intents for the broadcast receiver

        KEY = intent.getStringExtra(context.getResources().getString(R.string.survey_alarm_key));        // Gets the key identifier from the resource file
        followupValue = context.getResources().getString(R.string.followup_identifier);        // Gets the key identifier from the resource file
        endOfDayValue = context.getResources().getString(R.string.endofday_identifier);     // Gets the key identifier from the resource file

        systemInformation = new SystemInformation();        // Initializes the system information variable

        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," + "Alarm Manager Broadcaster" + (",") + "Activated for a Survey Instance";       // Data to be logged by the system
        dataLogger = new DataLogger(context, context.getResources().getString(R.string.subdirectory_logs), context.getResources().getString(R.string.system), data);        // Makes a new data logger item
        dataLogger.saveData("log");        // Logs the data

        dataLogger = new DataLogger(context, context.getResources().getString(R.string.subdirectory_information), context.getResources().getString(R.string.sleepmode), "SleepMode Checker");        // Makes a new data logger item
        checkEODDate = new DataLogger(context, context.getResources().getString(R.string.subdirectory_information), context.getResources().getString(R.string.eodmode), "Checking End Of Day File");        // Makes a new data logger item

        if (KEY.equalsIgnoreCase(followupValue))        // Checks if the value of the key is the same as the followup value
        {
            if (dataLogger.readData().contains("false"))        // Checks for the sleepmode level
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Alarm Manager Broadcaster" + (",") + "Starting Followup Survey" + ("\n");       // Data to be logged by the system

                surveyIntent = new Intent (context, FollowupSurvey.class);        // Calls an intent for a new activity
                surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       // Adds a new task for the service to start the activity
                context.startActivity(surveyIntent);        // Starts the activity specified
            }
            else        // If sleepmode is enabled
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Alarm Manager Broadcaster" + (",") + "Did NOT start Followup Survey due to SleepMode ENABLED" + ("\n");       // Data to be logged by the system
            }

            dataLogger = new DataLogger(context, context.getResources().getString(R.string.subdirectory_logs), context.getResources().getString(R.string.system), data);        // Makes a new data logger item
            dataLogger.saveData("log");        // Logs the data
        }

        if (KEY.equalsIgnoreCase(endOfDayValue) && !checkEODDate.readData().contains(systemInformation.getDateTime("yyyy/MM/dd")))     // Checks if the value of the key is the same as the end of day value
        {
            if (dataLogger.readData().contains("false"))        // Checks for the sleepmode level
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Alarm Manager Broadcaster" + (",") + "Starting End of Day Survey" + ("\n");       // Data to be logged by the system

                surveyIntent = new Intent (context, EndOfDaySurvey.class);        // Calls an intent for a new activity
                surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       // Adds a new task for the service to start the activity
                context.startActivity(surveyIntent);        // Starts the activity specified
            }
            else        // If sleepmode is enabled
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Alarm Manager Broadcaster" + (",") + "Did NOT start End of Day Survey due to SleepMode ENABLED" + ("\n");       // Data to be logged by the system
            }

            dataLogger = new DataLogger(context, context.getResources().getString(R.string.subdirectory_logs), context.getResources().getString(R.string.system), data);        // Makes a new data logger item
            dataLogger.saveData("log");        // Logs the data
        }
        else
        {
            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Alarm Manager Broadcaster" + (",") + "Dismissing Automatic End of Day Survey due to already completed" + ("\n");       // Data to be logged by the system
            dataLogger = new DataLogger(context, context.getResources().getString(R.string.subdirectory_logs), context.getResources().getString(R.string.system), data);        // Makes a new data logger item
            dataLogger.saveData("log");        // Logs the data
        }
    }
}
