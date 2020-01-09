package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.support.wearable.activity.WearableActivity;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The logic for the survey in regards to immediate pain. This activity is launched as soon as the start button in the watchface is pressed.
 * This survey only comes up with a button press and is not initiated by any timer or notification.
 */
public class PainSurvey extends WearableActivity
{
    private SharedPreferences sharedPreferences;        // Gets a reference to the shared preferences of the wearable activity
    private Vibrator vibrator;      // Gets a link to the system vibrator
    private int currentQuestion, answersTapped, index, hapticLevel;       // Initializes various integers to be used by the system
    private int[] userResponseIndex;        // This is the user response index that keeps track of the index response of the user.
    private Button back, next, answer;      // The buttons on the screen
    private TextView question;      // Links to the text shown on the survey screen
    private String role;        // Sets up all the string variable in the system
    private String[] userResponses;     // This is the user response.
    private String[] questions;     // This is the variable question that is assigned a position from the preference menu
    private String[][] answers;     // Based on the assigned questions the variable answer is modified.
    private SystemInformation systemInformation;        // Gets a reference to the system information class
    private ArrayList<String> responses;    // This is a string that is appended to.


    private final String[] caregiverQuestions =       // These are the questions for the care giver in order.
            {
                    "Is patient having pain now?",
                    "What is patient's pain level?",
                    "How distressed are you?",
                    "How distressed is the patient?",
                    "Did patient take an opioid for the pain?",
                    "Why not?",
                    "Ready to submit your answers?",
            };
    private final String[][] caregiverAnswers =       // These are the answers for the care giver in order.
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
                    {"Yes", "No", "Unsure"},
                    {"Not time yet", "Side effects", "Out of pills", "Worried taking too many", "Pain not bad enough", "Other Reason", "Unsure"},
                    {"Yes", "No"},
            };

    private final String[] patientQuestions =         // These are the patient questions in order.
            {
                    "Are you in pain now?",
                    "What is your pain level?",
                    "How distressed are you?",
                    "How distressed is your caregiver?",
                    "Did you take an opioid for the pain?",
                    "Why not?",
                    "Ready to submit your answers?",
            };
    private final String[][] patientAnswers =         // These are the patient answers in order.
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
                    {"Yes", "No"},
                    {"Not time yet", "Side effects", "Out of pills", "Worried taking too many", "Pain not bad enough", "Other Reason"},
                    {"Yes", "No"}
            };

    /**
     * This method is responsible for setting up the global items for tha activity in order to function properly.
     * @param savedInstance an instance of the activity from the superclass
     */
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);      // Makes a call to the super class method

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets a reference to the shared preferences of the activity
        this.systemInformation = new SystemInformation();       // Gets a reference to the system information of the wearable activity
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Initializes the vibrator variable

        this.decideRoleQuestions();      // Decides the role the device is playing
        this.userResponses = new String[questions.length];      // Sets up the responses needed by the user to be the length of the number given
        this.userResponseIndex = new int[userResponses.length];     // Sets up the index to be the integer value of the user responses length

        this.setContentView(R.layout.activity_ema);      // Sets the view of the watch to be the specified activity.

        this.back = findViewById(R.id.back);        // Gets a reference to the back button
        this.next = findViewById(R.id.next);        // Gets a reference to the next button
        this.answer = findViewById(R.id.answer);        // Gets a reference to the answer button
        this.question = findViewById(R.id.question);        // Gets a reference to the question text view

        this.hapticLevel = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("haptic_level", "")));       // Sets up the vibration level of the system for haptic feedback
        this.responses = new ArrayList<>();     // Initializes the array list of the responses by the user
        this.currentQuestion = 0;       // Sets the number of questioned answered by the user

        this.deploySurvey();        // Calls on the method for the survey to begin
    }

    /**
     * This method runs the survey as intended by the research team. The logic in terms of moving forward, going back, and changing the approach and movements through the
     * questions are all done in this method in as little steps as possible. This method is deployed as soon as the creation of the activity is complete
     */
    private void deploySurvey()
    {
        if (this.currentQuestion < questions.length)
        {
            if (this.currentQuestion == 0)       // Checks if the is the first question
            {
                this.next.setText(this.answers[0][0]);      // Sets the next button to be an answer choice
                this.back.setText(this.answers[0][1]);      // Sets the back button to be an answer choice
                this.answer.setVisibility(View.INVISIBLE);     // Removes the middle button option from the user
            }
            else
            {
                this.next.setText(getResources().getString(R.string.next_button));      // Sets the next text back to the original value
                this.back.setText(getResources().getString(R.string.back_button));      // Sets the back text to the original value
                this.answer.setVisibility(View.VISIBLE);        // Makes the answer button visible
            }

            this.question.setText(questions[this.currentQuestion]);     // Sets the question to be asked to be the current question position
            this.answersTapped = this.userResponseIndex[this.currentQuestion];      // Sets up the index of the answer tapped to be the response index of the current question
            this.responses.clear();     // Cleats the array list of any values in it

            Collections.addAll(this.responses, this.answers[this.currentQuestion]);     // Calls on the collections object to add all the values in the array list so it can remember them
            this.nextAnswer();      // Calls on the method to update the answer view

            this.next.setOnClickListener(new View.OnClickListener()         // Listens for the button to be clicked
            {
                @Override
                public void onClick(View v)         // When the button is clicked
                {
                    vibrator.vibrate(hapticLevel);      // Vibrates the system for the desired time

                    currentQuestion++;      // Increments the current question position
                    deploySurvey();     // Calls the method on itself to move the question forward
                }
            });

            this.back.setOnClickListener(new View.OnClickListener()         // Listens for the button to be clicked
            {
                @Override
                public void onClick(View v)         // When the button is clicked
                {
                    vibrator.vibrate(hapticLevel);      // Vibrates the system for the desired time

                    currentQuestion--;      // Increments the current question position
                    deploySurvey();     // Calls the method on itself to move the question forward
                }
            });

            this.answer.setOnClickListener(new View.OnClickListener()       // Sets a listener for the button
            {
                @Override
                public void onClick(View v)     // When the button is clicked
                {
                    vibrator.vibrate(hapticLevel);      // Vibrates the system for the desired time

                    answersTapped += 1;         // Increments the tap on the answer by the specified amount
                    nextAnswer();        // Calls on the method to update the answer view
                }
            });
        }
    }

    /**
     * This method decides the role that the watch is playing in terms of patient or caregiver and then based on the decision makes the questions and answers
     * used in the survey to be parallel with what is needed by the user.
     */
    private void decideRoleQuestions()
    {
        this.role = this.sharedPreferences.getString("user_info", "");

        assert this.role != null;       // Makes sure that the role variable is not a null value
        if(this.role.equalsIgnoreCase("PT"))        // Checks the role value against a patient identifier
        {
            this.questions = this.patientQuestions;     // Sets the questions to be that of the patient
            this.answers = this.patientAnswers;     // Sets the answers to be that of the patient
        }
        else if (this.role.equalsIgnoreCase("CG"))      // Checks the role value against a caregiver identifier
        {
            this.questions = this.caregiverQuestions;       // Sets the questions to be that of the patient
            this.answers = this.caregiverAnswers;       // Sets the answers to be that of the caregiver
        }
    }

    /**
     * This method gets the next answer in the list of answers given. If the answer is the last answer, it goes back to the start of the list
     * @return ths index that the user is using as the answer
     */
    private int nextAnswer()
    {
        this.index = this.answersTapped%this.responses.size();      // Sets up the index that the user is currently on
        this.answer.setText(this.responses.get(this.index));        // Sets the answer choice seen by the user to be that of the index in the answer choice
        return index;       // Returns the index to where the method was called
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
