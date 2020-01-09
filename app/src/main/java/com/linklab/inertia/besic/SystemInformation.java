package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.content.Intent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

class SystemInformation
{
    private DateFormat dateTimeFormat;      // Private date format variables
    private Date current;       // Private date variables
    private boolean sleepMode;      // Private boolean variable for the sleep level of the application
    private int level, scale, batteryPercent;       // private integer variables

    /**
     * Constructor for the class. Initializes the variables
     */
    SystemInformation()
    {
        this.level = 0;     // Sets the level
        this.scale = 0;     // Sets the scale
        this.batteryPercent = 0;        // Sets the battery percentage
        this.sleepMode = false;     // Sets the initial sleepMode of the system
    }

    /**
     * Gets the current date and time for the survey to be stamped with
     * @return a string format of the date and time
     */
    String getDateTime(String pattern)
    {
        this.setCurrent(new Date());        // The current date and time is set
        this.setDateTimeFormat(new SimpleDateFormat(pattern, Locale.getDefault()));       // The date and time is processed in the format requested
        return this.getDateTimeFormat().format(getCurrent());       // The information is returned to the requester
    }

    /**
     * This get the battery level from the system
     * @param context gets the application context needed
     * @return an integer value of the battery level
     */
    int getBatteryLevel(Context context)
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        this.setLevel(Objects.requireNonNull(batteryStatus).getIntExtra(BatteryManager.EXTRA_LEVEL, -1));      // Initializes an integer value for the battery level
        this.setScale(batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));      // Scales the battery level to 100 from whatever default value it is.
        this.setBatteryPercent((getLevel()*100)/getScale());     // Sets the battery level to a percentage of what it needs to be.

        return this.getBatteryPercent();      // This is the battery level as a string
    }

    /**
     * This method checks if the current time parameter (in military format) is between two given time limits.
     * @param currentTime is the current time in military format
     * @param startHour the starting hour of the beginning time
     * @param endHour the ending hour of the time needed to be checked against
     * @param startMinute the starting minute of the beginning time
     * @param endMinute the ending minute of the checked time
     * @param startSecond the starting second of the time
     * @param endSecond the ending second of the time to be checked against
     * @return a boolean true if the system time is between the two given times, otherwise false.
     */
    boolean isTimeBetweenTimes(String currentTime, int startHour, int endHour, int startMinute, int endMinute, int startSecond, int endSecond)     // Checks if the current time is between two times
    {
        String hourString = currentTime.split(":")[0];     // It set the first string to the hour
        String minuteString = currentTime.split(":")[1];       // It sets the second string to the minutes
        String secondString = currentTime.split(":")[2];       // It sets the third string to the seconds

        int hour = Integer.parseInt(hourString);        // Makes the string an integer for the hour
        int minute = Integer.parseInt(minuteString);        // Makes the string an integer for the minute
        int second = Integer.parseInt(secondString);        // Makes the string an integer for the seconds

        if ((hour >= startHour) && (hour < endHour))    // If the time of the system is between the given time limits
        {
            return true;        // Return true
        }
        else        // If it fails the first check
        {
            if (hour == endHour)        // Check if the hour is the current hour
            {
                if ((minute >= startMinute) && (minute < endMinute))      // Check the minute, and if it is less than the end minute time
                {
                    return true;        // Return true
                }
                else        // If it fails the first check
                {
                    if (minute == endMinute)        // Check if the minute is the current minute
                    {
                        return (second >= startSecond) && (second < endSecond);
                    }
                    return false;       // If not return false
                }
            }
            return false;       // If not return false
        }
    }

    /**
     * Sets the level of the input
     * @param levelInput is the level required
     */
    private void setLevel(int levelInput)
    {
        this.level = levelInput;        // Sets the variable appropriately
    }

    /**
     * Sets the scale of the input
     * @param scaleInput is the scale required
     */
    private void setScale(int scaleInput)
    {
        this.scale = scaleInput;        // Sets the variable appropriately
    }

    /**
     * Sets the battery percentage to the input
     * @param batteryPercentInput is the battery input acquired
     */
    private void setBatteryPercent (int batteryPercentInput)
    {
        this.batteryPercent = batteryPercentInput;        // Sets the variable appropriately
    }

    /**
     * Sets the date format of the input
     * @param dateTimeFormat the date format argument
     */
    private void setDateTimeFormat(SimpleDateFormat dateTimeFormat)
    {
        this.dateTimeFormat = dateTimeFormat;        // Sets the variable appropriately
    }

    /**
     * Sets te current date to the input parameter
     * @param currentDate is the date needed to set to
     */
    private void setCurrent(Date currentDate)
    {
        this.current = currentDate;        // Sets the variable appropriately
    }

    /**
     * Sets the sleepMode of the system
     * @param sleepMode is the level needed to be changed to
     */
    void setSleepMode(boolean sleepMode)
    {
        this.sleepMode = sleepMode;        // Sets the variable appropriately
    }

    /**
     * Gets the level
     * @return the level variable value
     */
    private int getLevel()
    {
        return this.level;      // Returns the variable
    }

    /**
     * Gets the scale
     * @return the scale variable value
     */
    private int getScale()
    {
        return this.scale;      // Returns the variable
    }

    /**
     * Gets the battery percentage value
     * @return the battery percent
     */
    private int getBatteryPercent()
    {
        return this.batteryPercent;      // Returns the variable
    }

    /**
     * Gets the date format
     * @return the date format variable
     */
    private DateFormat getDateTimeFormat()
    {
        return this.dateTimeFormat;      // Returns the variable
    }

    /**
     * Gets the current value
     * @return the current variable value
     */
    private Date getCurrent()
    {
        return this.current;      // Returns the variable
    }

    /**
     * Creates a global access to the sleepMode variable
     * @return the value associated with the sleepMode
     */
    boolean getSleepMode()
    {
        return this.sleepMode;      // Returns the value associated with the variable
    }
}
