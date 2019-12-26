package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.graphics.*;
import android.text.*;
import android.view.*;

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
        private SystemInformation systemInformation;        // Gets a context to te system information class.
        private Paint.FontMetrics startBackground;
        private TextPaint batteryPaint, timePaint, datePaint, startPaint;     // Sets the paint instance for the battery level text.
        private String batteryLevel, currentTime, currentDate, startMessage;        // Sets up string variables.
        private Rect batteryLevelTextBounds, currentTimeTextBounds, currentDateTextBounds;        // Sets up bounds for items on canvas.
        private int batteryLevelPositionX, batteryLevelPositionY,
                currentTimePositionX, currentTimePositionY, currentDatePositionX, currentDatePositionY,
                startX, startY;       // Sets up integer variables.

        /**
         * This method is called when the service of the watch face is called for the first time.
         * This overrides the super onCreate method.
         * @param holder is the holder that is needed to create the system.
         */
        @Override
        public void onCreate(SurfaceHolder holder)
        {
            super.onCreate(holder);     // Calls a creation instance

            this.systemInformation = new SystemInformation();       // Binds the variable to the calls in the class

            this.startBackground = new Paint.FontMetrics();     // Sets the background of the button
            this.batteryPaint = new TextPaint();        // Makes a text paint
            this.timePaint = new TextPaint();        // Makes a text paint
            this.datePaint = new TextPaint();        // Makes a text paint
            this.startPaint = new TextPaint();        // Makes a text paint

            this.batteryLevelTextBounds = new Rect();        // Makes a text rectangle
            this.currentTimeTextBounds = new Rect();        // Makes a text rectangle
            this.currentDateTextBounds = new Rect();        // Makes a text rectangle

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

            canvas.drawRect(this.startX, startY, (getResources().getDisplayMetrics().widthPixels / 2)+(getResources().getDisplayMetrics().widthPixels / 15),    // Draws the specified rectangle
                    this.batteryLevelPositionY-this.batteryLevelTextBounds.height()-15, this.startPaint);       // Continued from previous line
            this.reconfigureButtons();      // Calls the method
            canvas.drawText(this.startMessage, this.startX+20, startY + (startY/3) + 12, this.startPaint);      // Calls the canvas to draw the message information

            onTimeTick();       // Calls the specified method
        }

        /**
         * This method sets up buttons on the screen
         */
        private void setUpButtons()
        {
            this.startMessage = getResources().getString(R.string.start_string);        // Sets the string of the button
            this.startX = 0;        // Sets the starting x location
            this.startY = this.currentTimePositionY+(this.currentTimeTextBounds.height()/3)+10;        // Sets the starting y location

            this.startPaint.setTextSize(40);        // Initializes button size
            this.startPaint.getFontMetrics(this.startBackground);       // Sets background

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
        private void setUpDefaultColors()
        {
            if (isScreenOn())      // Checks if the system is in Ambient mode
            {
                this.datePaint.setColor(Color.WHITE);       // Sets the color of the date on the UI
                this.timePaint.setColor(Color.WHITE);       // Sets the color of the time on the UI.
                this.batteryPaint.setColor(Color.GREEN);        // Sets the color of the battery level.
            }
            else        // If the screen is in ambient mode
            {
                this.datePaint.setColor(Color.DKGRAY);       // Sets the color of the date on the UI
                this.timePaint.setColor(Color.LTGRAY);       // Sets the color of the time on the UI.
                this.batteryPaint.setColor(Color.DKGRAY);        // Sets the color of the battery level.
            }

            if (this.getBatteryLevelInteger() <= 30)        // Checks te battery level
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

            if (isScreenOn())       // Checks if the screen is on
            {
                this.startPaint.setColor(Color.WHITE);      // Sets the color
            }
            else
            {
                this.startPaint.setColor(Color.BLACK);      // Sets the color
            }
        }

        /**
         * This method initializes the required values for variables needed in the onDraw method.
         */
        private void setUpDefaultValues()
        {
            this.currentDate = this.systemInformation.getDateForUI();        // Sets up the date from the specific method.
            this.currentTime = this.systemInformation.getTimeForUI();        // Sets up the time from the specific method.
            this.batteryLevel = this.getBatteryLevelString();      // Sets up the battery level by calling the specified method.
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