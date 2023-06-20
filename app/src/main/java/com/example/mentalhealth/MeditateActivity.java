package com.example.mentalhealth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MeditateActivity extends AppCompatActivity {

    private TextView textViewHighestTimer;
    private EditText editTextTimer;
    private Button buttonStart;
    private TextView textViewCountdown;
    private Button buttonGuidedMeditation;

    private CountDownTimer countDownTimer;
    private long timerDuration = 0;
    private long highestTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meditate);

        textViewHighestTimer = findViewById(R.id.textViewHighestTimer);
        editTextTimer = findViewById(R.id.editTextTimer);
        buttonStart = findViewById(R.id.buttonStart);
        textViewCountdown = findViewById(R.id.textViewCountdown);
        buttonGuidedMeditation = findViewById(R.id.buttonGuidedMeditation);

        // Fetch the highest timer from the database and display it
        highestTimer = fetchHighestTimerFromDatabase();
        textViewHighestTimer.setText("Highest Timer: " + highestTimer + " seconds");

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        buttonGuidedMeditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGuidedMeditation();
            }
        });
    }

    private void startTimer() {
        String timerValue = editTextTimer.getText().toString();

        if (timerValue.isEmpty()) {
            showToast("Please enter a timer value.");
            return;
        }

        long inputTimer = Long.parseLong(timerValue);
        if (inputTimer <= 0) {
            showToast("Please enter a valid timer value.");
            return;
        }

        // Update the highest timer if necessary
        if (inputTimer > highestTimer) {
            highestTimer = inputTimer;
            saveHighestTimerToDatabase(highestTimer);
            textViewHighestTimer.setText("Highest Timer: " + highestTimer + " seconds");
        }

        // Start the countdown timer
        timerDuration = inputTimer * 1000;
        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                textViewCountdown.setText("Countdown: " + secondsRemaining + " seconds");
            }

            @Override
            public void onFinish() {
                textViewCountdown.setText("Countdown: 0 seconds");
                showToast("Timer finished!");
            }
        };

        countDownTimer.start();
    }

    private void startGuidedMeditation() {
        // TODO: Implement the logic for starting the guided meditation
        showToast("Start guided meditation");
    }

    private long fetchHighestTimerFromDatabase() {
        // TODO: Implement the logic to fetch the highest timer from the database
        // You can use SQLite, Room, or any other database framework

        // Dummy implementation, returning 0 for now
        return 0;
    }

    private void saveHighestTimerToDatabase(long timer) {
        // TODO: Implement the logic to save the highest timer to the database
        // You can use SQLite, Room, or any other database framework
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
