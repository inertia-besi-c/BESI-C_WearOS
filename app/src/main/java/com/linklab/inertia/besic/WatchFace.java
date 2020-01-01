package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.wearable.watchface.*;
import android.graphics.*;
import android.text.*;
import android.view.*;
import android.widget.*;

/**
 * On Android Wear Watch Face is implemented as a service. This is being used by the application to save resources by giving them to the android system to configure.
 * Every Watch Face Service extends from the CanvasWatchFaceService class and provides a com.linklab.inertia.besic.WatchFace Engine.
 */
public class WatchFace extends CanvasWatchFaceService
{
    /**
     * This class is the default engine for the canvas.
     * @return a new instance of the private class to draw the watch face.
     */
    @Override
    public Engine onCreateEngine()
    {
        return new BESIWatchFace();        // Calls the manually created private engine in the class.
    }

    /**
     * This engine is responsible for the Drawing of the watch face and receives events from the system regarding what is needed to run.
     */
    private class BESIWatchFace extends CanvasWatchFaceService.Engine
    {
        private SharedPreferences sharedPreferences;        // Gets a context to the system shared preferences object
        private Vibrator vibrator;      // This is the variable that access the vibrator in the device
        private DataLogger dataLogger;
        private SystemInformation systemInformation;        // Gets a context to the system information class
        private Paint.FontMetrics startBackground, sleepEODEMABackground;      // Sets variables background
        private TextPaint batteryPaint, timePaint, datePaint, startPaint, sleepEODEMAPaint;     // Sets the paint instance for the texts
        private String batteryLevel, currentTime, currentDate, startMessage, sleepEODEMAMessage;        // Sets up string variables
        private Rect batteryLevelTextBounds, currentTimeTextBounds, currentDateTextBounds;        // Sets up bounds for items on canvas
        private boolean drawEODEMA;      // Sets up all the boolean to be run on the system
        private int batteryLevelPositionX, batteryLevelPositionY,
                currentTimePositionX, currentTimePositionY, currentDatePositionX, currentDatePositionY,
                startX, startY, sleepEODEMAX, sleepEODEMAY, hapticLevel, count;       // Sets up integer variables.

        /**
         * This method is called when the service of the watch face is called for the first time.
         * This overrides the super onCreate method.
         * @param holder is the holder that is needed to create the system.
         */
        @Override
        public void onCreate(SurfaceHolder holder)
        {
            super.onCreate(holder);     // Calls a creation instance

            this.setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this).setAcceptsTapEvents(true).build());        // Sets the watchface to accept user tap event inputs.
            this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Get instance of Vibrator from current Context

            this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preferences from the shared preference object.
            this.systemInformation = new SystemInformation();       // Binds the variable to the calls in the class

            this.startBackground = new Paint.FontMetrics();     // Sets the background of the button
            this.sleepEODEMABackground = new Paint.FontMetrics();       // Sets the background of the sleep/EODEMA button

            this.batteryPaint = new TextPaint();        // Makes a text paint
            this.timePaint = new TextPaint();        // Makes a text paint
            this.datePaint = new TextPaint();        // Makes a text paint
            this.startPaint = new TextPaint();        // Makes a text paint
            this.sleepEODEMAPaint = new TextPaint();        // Makes a text paint

            this.batteryLevelTextBounds = new Rect();        // Makes a text rectangle
            this.currentTimeTextBounds = new Rect();        // Makes a text rectangle
            this.currentDateTextBounds = new Rect();        // Makes a text rectangle

            this.drawEODEMA = false;     // Initializes the boolean as a false value

            this.setUpDefaultValues();      // Calls the method
            this.setUpDefaultColors();      // Calls the method
        }

        /**
         * This is the method that draws all the element that is needed on the screen.
         * @param canvas specifies what is being drawn on
         * @param bounds sets the limits of the screen to be drawn on
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds)
        {
            super.onDraw(canvas, bounds);       // Calls a drawing instance.

            this.setUpDefaultValues();      // Sets up the values on the UI.
            this.setUpDefaultColors();      // Sets up the colors on the UI.
            this.setUpDateAndTime();       // Sets up the time on the UI.
            this.setUpBatteryLevel();      // Sets up the battery values on the UI.
            this.setUpButtons();        // Sets up the buttons on the UI.

            this.clearCanvas(canvas);       // Clears the screen so new values can be drawn.

            canvas.drawText(this.currentDate, this.currentDatePositionX, this.currentDatePositionY, this.datePaint);       // Calls the canvas to draw the date information
            canvas.drawText(this.currentTime, this.currentTimePositionX, this.currentTimePositionY, this.timePaint);       // Calls the canvas to draw the time information
            canvas.drawText(this.batteryLevel, this.batteryLevelPositionX, this.batteryLevelPositionY, this.batteryPaint);      // Calls the canvas to draw the battery information.

            canvas.drawRect(this.startX, this.startY, this.sleepEODEMAX,    // Draws the specified rectangle
                    this.batteryLevelPositionY-this.batteryLevelTextBounds.height()-15, this.startPaint);       // Continued from previous line
            canvas.drawRect(this.sleepEODEMAX, this.sleepEODEMAY, getResources().getDisplayMetrics().widthPixels,       // Draws the specified rectangle
                    this.batteryLevelPositionY-this.batteryLevelTextBounds.height()-15, this.sleepEODEMAPaint);       // Continued from previous line
            this.reconfigureButtons();      // Calls the method
            canvas.drawText(this.startMessage, this.startX+20, this.startY + (this.startY/3) + 12, this.startPaint);      // Calls the canvas to draw the message information
            canvas.drawText(this.sleepEODEMAMessage, this.sleepEODEMAX+8, this.sleepEODEMAY+(this.sleepEODEMAY/3)+8, this.sleepEODEMAPaint);        // Calls the canvas to draw the message
        }

        /**
         * Waits for the screen to be tapped by the user. Then does case analysis to determine the appropriate action needed.
         * @param tapType is the type of tap performed on the screen
         * @param x is the x location on the screen where the tap was performed
         * @param y is the y location on the screen where the tap was performed
         * @param eventTime is how long the type of tap was performed for
         */
        @Override
        public void onTapCommand(@TapType int tapType, int x, int y, long eventTime)
        {
            int startButtonXEnd = (getResources().getDisplayMetrics().widthPixels / 2)+(getResources().getDisplayMetrics().widthPixels / 15);       // The end of the start button x location
            int sleepEODEMAXEnd = getResources().getDisplayMetrics().widthPixels;

            int buttonsYEnd = this.batteryLevelPositionY-this.batteryLevelTextBounds.height()-15;       // The end of the start button y location

            switch (tapType)        // Switch case for the tap type
            {
                case WatchFaceService.TAP_TYPE_TOUCH:       // Checks if the tap type was a touch
                    if (x >= startX && x < startButtonXEnd && y >= startY && y <= buttonsYEnd)     // Determines if this was around the start button
                    {
                        if (count==0)       // If this is the first time we are opening the app
                        {
                            this.vibrator.vibrate(hapticLevel);     // Vibrates the system for the specified time
                            startActivity(new Intent(WatchFace.this, Settings.class));      // Open the settings for them to be set
                            this.count++;        // Increment the count variable
                        }
                        else    // If not, we have launched the app before.
                        {
                            this.vibrator.vibrate(hapticLevel);     // Vibrates the system for the specified time

                            this.dataLogger = new DataLogger(getApplicationContext(),"TestSubdirectory", "TestFile", "Test Content of File, Pain Button");
                            this.dataLogger.saveData("log");

                            // This is where the pain EMA would be started.
                            Toast.makeText(getApplicationContext(), "Settings already set!", Toast.LENGTH_LONG).show();     // Shows a toast that settings have already been done
                        }
                    }

                    if (x > startButtonXEnd && x < sleepEODEMAXEnd && y >= sleepEODEMAY && y <= buttonsYEnd)
                    {
                        if (drawEODEMA)     // Checks if the daily ema button needs is available
                        {
                            this.vibrator.vibrate(hapticLevel);     // Vibrates the system for the specified time
                            // This is where an intent to launch the end of day ema would be made
                            Toast.makeText(getApplicationContext(), "EODEMA not Implemented!", Toast.LENGTH_LONG).show();     // Shows a toast that settings have already been done
                        }
                        else
                        {
                            this.vibrator.vibrate(hapticLevel);     // Vibrates the system for the specified time
                            setSleepMode(!getSleepMode());     // Sets the sleepMode level to be altered
                            invalidate();       // Immediately updates the screen
                        }
                    }

                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:        // Checks if the user dismissed the touch
                    break;      // Breaks the tap action
            }
        }

        /**
         * This method sets up buttons on the screen
         */
        private void setUpButtons()
        {
            this.startX = 0;        // Sets the starting x location
            this.sleepEODEMAX = (getResources().getDisplayMetrics().widthPixels / 2)+(getResources().getDisplayMetrics().widthPixels / 15);     // Sets the starting x location

            this.startY = this.currentTimePositionY+(this.currentTimeTextBounds.height()/3)+10;        // Sets the starting y location
            this.sleepEODEMAY = this.currentTimePositionY+(this.currentTimeTextBounds.height()/3)+10;     // Sets the starting y location

            this.startPaint.setTextSize(Integer.valueOf(getResources().getString(R.string.ui_start_button_size)));        // Initializes button size
            this.sleepEODEMAPaint.setTextSize(Integer.valueOf(getResources().getString(R.string.ui_sleep_button_size)));        // Sets the sleep button text size

            this.startPaint.getFontMetrics(this.startBackground);       // Sets background
            this.sleepEODEMAPaint.getFontMetrics(this.sleepEODEMABackground);       // Sets background

            this.startMessage = getResources().getString(R.string.start_string);        // Sets the string of the button

            drawStartButton();      // Calls the method
            decideSleepEODEMAButton();      // Calls the method

            if (drawEODEMA)     // If it is time to draw the end of day EMA
            {
                this.sleepEODEMAMessage = getResources().getString(R.string.eodema_string);      // Sets the string of the button
                drawEODEMAButton();      // Calls the method
            }
            else        // If not, draw the sleep automatically
            {
                this.sleepEODEMAMessage = getResources().getString(R.string.sleep_string);      // Sets the string of the button
                drawSleepButton();      // Calls the method
            }
        }

        /**
         * This method sets up the Date and Time information needed such as screen location, text, and paint.
         */
        private void setUpDateAndTime()
        {
            this.timePaint.getTextBounds(this.currentTime, 0, this.currentTime.length(), this.currentTimeTextBounds);         // Prints the time information
            this.datePaint.getTextBounds(this.currentDate, 0, this.currentDate.length(), this.currentDateTextBounds);         // Prints the date information

            this.timePaint.setTextSize(Float.valueOf(getResources().getString(R.string.ui_time_size)));     // Sets the size of the UI element
            this.datePaint.setTextSize(Float.valueOf(getResources().getString(R.string.ui_date_size)));     // Sets the size of the UI element

            this.currentTimePositionX = Math.abs((getResources().getDisplayMetrics().widthPixels / 2) - (this.currentTimeTextBounds.width()/2) - 5);        // Sets te x location of the time.
            this.currentDatePositionX = Math.abs((getResources().getDisplayMetrics().widthPixels / 2) - (this.currentDateTextBounds.width()/2));        // Sets te x location of the date.

            this.currentTimePositionY = Math.abs((getResources().getDisplayMetrics().heightPixels / 2) - 15);     // Sets the y location of the time.
            this.currentDatePositionY = Math.abs((getResources().getDisplayMetrics().heightPixels / 2) - ((this.currentDateTextBounds.height()*2) + 20) - 15);     // Sets the y location of the date.
        }

        /**
         * This method sets up the battery level information needed such as screen location, text, and paint.
         */
        private void setUpBatteryLevel()
        {
            this.batteryPaint.getTextBounds(this.batteryLevel, 0, this.batteryLevel.length(), this.batteryLevelTextBounds);       // Paints the battery information
            this.batteryPaint.setTextSize(Float.valueOf(getResources().getString(R.string.ui_battery_size)));     // Sets the size of the UI element
            this.batteryLevelPositionX = Math.abs((getResources().getDisplayMetrics().widthPixels / 2) - (this.batteryLevelTextBounds.width()/2));      // Sets the x location of the battery level
            this.batteryLevelPositionY = Math.abs(getResources().getDisplayMetrics().heightPixels) - (this.batteryLevelTextBounds.height()/2 + 10);     // Sets the y location of the battery level
        }

        /**
         * This method initializes the required colors for variables needed in the onDraw method.
         */
        @SuppressWarnings("ALL")        // Suppresses the warnings for this method
        private void setUpDefaultColors()
        {
            if (isScreenOn())      // Checks if the system is in Ambient mode
            {
                this.datePaint.setColor(Color.WHITE);       // Sets the color of the date on the UI
                this.timePaint.setColor(Color.WHITE);       // Sets the color of the time on the UI.
                this.batteryPaint.setColor(Color.GREEN);        // Sets the color of the battery level.
                this.startPaint.setColor(Color.GREEN);      // Sets color of the start button to this level
                this.sleepEODEMAPaint.setColor(Color.BLUE);      // Sets color of the button to this level
            }
            else        // If the screen is in ambient mode
            {
                this.datePaint.setColor(Color.DKGRAY);       // Sets the color of the date on the UI
                this.timePaint.setColor(Color.LTGRAY);       // Sets the color of the time on the UI.
                this.batteryPaint.setColor(Color.DKGRAY);        // Sets the color of the battery level.
                this.startPaint.setColor(Color.DKGRAY);      // Sets color of the start button to this level
                this.sleepEODEMAPaint.setColor(Color.DKGRAY);      // Sets color of the button to this level
            }

            if (this.getBatteryLevelInteger() <= Integer.valueOf(this.sharedPreferences.getString("low_battery_alert", "")))        // Checks the battery level
            {
                this.batteryPaint.setColor(Color.RED);        // Sets the color of the battery level.
            }
        }

        /**
         * Resets button variables so text can be drawn using the same resources
         */
        private void reconfigureButtons()
        {
            this.startPaint.setTextSize(Integer.valueOf(getResources().getString(R.string.ui_start_button_size)));      // Sets the text size

            if (drawEODEMA)     // If it is time to draw the end of day ema button
            {
                this.sleepEODEMAPaint.setTextSize(Integer.valueOf(getResources().getString(R.string.ui_survey_button_size)));    // Sets the text size
            }
            else        // If not, draw the sleep button attribute automatically.
            {
                this.sleepEODEMAPaint.setTextSize(Integer.valueOf(getResources().getString(R.string.ui_sleep_button_size)));    // Sets the text size
            }

            if (isScreenOn())       // Checks if the screen is on
            {
                this.startPaint.setColor(Color.WHITE);      // Sets the color
                this.sleepEODEMAPaint.setColor(Color.WHITE);        // Sets the color
            }
            else
            {
                this.startPaint.setColor(Color.BLACK);      // Sets the color
                this.sleepEODEMAPaint.setColor(Color.BLACK);        // Sets the color
            }
        }

        /**
         * This method checks with the system information and decides if the sleep button or the daily ema button should be displayed.
         */
        @SuppressWarnings("ALL")        // Suppresses the warnings for this method
        private void decideSleepEODEMAButton()
        {
            int startHour = Integer.valueOf(this.sharedPreferences.getString("eod_manual_start_hour", ""));     // Gets the start hour from preferences
            int startMinute = Integer.valueOf(this.sharedPreferences.getString("eod_manual_start_minute", ""));     // Gets the start minute from preferences
            int startSecond = Integer.valueOf(this.sharedPreferences.getString("eod_manual_start_second", ""));     // Gets the start second from preferences

            int endHour = Integer.parseInt(this.sharedPreferences.getString("eod_manual_end_hour", ""));         // Gets the end hour from preferences
            int endMinute = Integer.valueOf(this.sharedPreferences.getString("eod_manual_end_minute", ""));     // Gets the end minute from preferences
            int endSecond = Integer.valueOf(this.sharedPreferences.getString("eod_manual_end_second", ""));     // Gets the end second from preferences

            drawEODEMA = systemInformation.isTimeBetweenTimes(systemInformation.getTimeMilitary(), startHour, endHour, startMinute, endMinute, startSecond, endSecond);     // Calls the deciding method
        }

        /**
         * Draws the sleep button based on the system conditions
         */
        private void drawSleepButton()
        {
            if (isScreenOn())       // Checks if the screen is on on the device
            {
                if (getSleepMode())     // Checks if sleep mode on the system is not enabled
                {
                    this.sleepEODEMAPaint.setColor(Color.GRAY);      // Sets color to this level
                }
                else        // if sleep mode is enabled
                {
                    this.sleepEODEMAPaint.setColor(Color.BLUE);      // Sets color to this level
                }
            }
            else        // If the screen is off on the device
            {
                this.sleepEODEMAPaint.setColor(Color.DKGRAY);      // Sets color to this level
            }
        }

        /**
         * Draws the end of day ema button based on the system attributes
         */
        private void drawEODEMAButton()
        {
            if (isScreenOn())       // Checks if the screen is on on the device
            {
                this.sleepEODEMAPaint.setColor(Color.RED);      // Sets color to this level
            }
            else        // If not, sets the following
            {
                this.sleepEODEMAPaint.setColor(Color.DKGRAY);      // Sets color to this level
            }
        }

        /**
         * Draws the start button onto the screen.
         */
        private void drawStartButton()
        {
            if (isScreenOn())       // If the screen is on
            {
                this.startPaint.setColor(Color.GREEN);      // Sets color to this level
            }
            else        // If screen is off
            {
                this.startPaint.setColor(Color.DKGRAY);     // Sets color to this level
            }
        }

        /**
         * This method initializes the required values for variables needed in the onDraw method.
         */
        @SuppressWarnings("ALL")        // Suppresses the warnings for this method
        private void setUpDefaultValues()
        {
            this.currentDate = this.systemInformation.getDateForUI();        // Sets up the date from the specific method.
            this.currentTime = this.systemInformation.getTimeForUI();        // Sets up the time from the specific method.
            this.batteryLevel = this.getBatteryLevelString();      // Sets up the battery level by calling the specified method.
            this.hapticLevel = Integer.valueOf(this.sharedPreferences.getString("haptic_level", ""));
        }

        /**
         * Overridden method updates the time every minute
         */
        @Override
        public void onTimeTick()
        {
            super.onTimeTick();     // Calls to superclass
            invalidate();       // Redraws the screen
        }

        /**
         * This method changes the sleepMode level of the system
         */
        private void setSleepMode(boolean bool)
        {
            this.systemInformation.setSleepMode(bool);        // Calls to the information class for the sleepMode level to be changed
        }

        /**
         * This method gives the string value modified for battery level text.
         * @return the string needed to set up the battery level.
         */
        private String getBatteryLevelString()
        {
            return getResources().getString(R.string.battery_level_string) + " " + getBatteryLevelInteger() + "%";      // Sets up the string shown on the canvas for battery level.
        }

        /**
         * This method gives the integer value needed for battery level.
         * @return the integer needed to set up the battery level.
         */
        private int getBatteryLevelInteger()
        {
            return this.systemInformation.getBatteryLevel(getApplicationContext());     // Gets the battery level as an integer from the helper class
        }

        /**
         * This method provides the sleepMode of the system
         * @return a boolean checking if sleepMode is enabled
         */
        private boolean getSleepMode()
        {
            return systemInformation.getSleepMode();        // Calls to the information class for the sleepMode level
        }

        /**
         * This method erases everything on the watchface
         * @param canvas is which face is being erased
         */
        private void clearCanvas(Canvas canvas)
        {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);     // Clears the current face
        }

        /**
         * Checks if the screen is on
         * @return if the screen is on and not in ambient mode
         */
        private boolean isScreenOn()
        {
            return isVisible() && !isInAmbientMode();       // Checks the ambient mode level and the visibility
        }
    }
}