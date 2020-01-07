package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

/**
 * The logic for the survey in regards to immediate pain. This activity is launched as soon as the start button in the watchface is pressed.
 * This survey only comes up with a button press and is not initiated by any timer or notification.
 */
public class PainSurvey extends WearableActivity
{
    /**
     * This method is responsible for setting up the global items for tha activity in order to function properly.
     * @param savedInstance an instance of the activity from the superclass
     */
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);      // Makes a call to the super class method

        setContentView(R.layout.activity_ema);      // Sets the view of the watch to be the specified activity.
    }

    /**
     * This method is called to clean up the method by removing all floating variables and timers that are not needed anymore
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();      // Calls the super class method
    }
}
