package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.*;
import android.os.*;
import java.text.*;
import java.util.*;

class SystemInformation
{
    private DateFormat timeFormat, dateFormat;
    private Date current;
    private int level, scale, batteryPercent;

    /**
     * Constructor for the class. Initializes the variables
     */
    SystemInformation()
    {
        this.level = 0;     // Sets the level
        this.scale = 0;     // Sets the scale
        this.batteryPercent = 0;        // Sets the battery percentage
    }

    /**
     * This gets only the current time from the system
     * @return a time format designed for the UI.
     */
    String getTimeForUI()
    {
        this.setCurrent(new Date());     // Resets te current variable to be a new date
        this.setTimeFormat(new SimpleDateFormat("h:mm a", Locale.getDefault()));      // The time format is called in default format
        return this.getTimeFormat().format(getCurrent());       // The current time is set to show on the time text view.
    }

    /**
     * This gets only the current date from the system
     * @return a date format for the UI.
     */
    String getDateForUI()
    {
        this.setCurrent(new Date());     // Resets te current variable to be a new date
        this.setDateFormat(new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()));     // The date is called in US format.
        return this.getDateFormat().format(getCurrent());       // The current date is set to show on the date text view.
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
     * Sets the time format of the input
     * @param timeFormat the time format argument
     */
    private void setTimeFormat(SimpleDateFormat timeFormat)
    {
        this.timeFormat = timeFormat;        // Sets the variable appropriately
    }

    /**
     * Sets the date format of the input
     * @param dateFormat the date format argument
     */
    private void setDateFormat(SimpleDateFormat dateFormat)
    {
        this.dateFormat = dateFormat;        // Sets the variable appropriately
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
     * Gets the time format
     * @return the time format variable value
     */
    private DateFormat getTimeFormat()
    {
        return this.timeFormat;      // Returns the variable
    }

    /**
     * Gets the date format
     * @return the date format variable
     */
    private DateFormat getDateFormat()
    {
        return this.dateFormat;      // Returns the variable
    }

    /**
     * Gets the current value
     * @return the current variable value
     */
    private Date getCurrent()
    {
        return this.current;      // Returns the variable
    }
}