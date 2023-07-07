package com.example.mentalhealth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;

import android.widget.ProgressBar;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import java.util.Locale;

public class MeditateActivity extends AppCompatActivity {

    private TextView textViewHighestTimer;
    private EditText editTextTimer;
    private ToggleButton buttonStart;
    private TextView textViewCountdown;
    private Button buttonGuidedMeditation;

    private CountDownTimer countDownTimer;
    private long timerDuration = 0;
    private long highestTimer = 0;
    private CircularProgressBar circularProgressBar;
    private Handler handler;
    private TextToSpeech textToSpeech;
    private boolean is_using_timer=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meditate);
        FirebaseApp.initializeApp(this);

        textViewHighestTimer = findViewById(R.id.textViewHighestTimer);
        editTextTimer = findViewById(R.id.editTextTimer);
        buttonStart = findViewById(R.id.buttonStart);
        textViewCountdown = findViewById(R.id.textViewCountdown);
        buttonGuidedMeditation = findViewById(R.id.buttonGuidedMeditation);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.timerProgress);

        circularProgressBar.setProgress(0);
        int colorInt = android.graphics.Color.parseColor("#ff8231");
        circularProgressBar.setProgressColor(colorInt);



        // Fetch the highest timer from the database and display it
        fetchHighestTimerFromDatabase();
        handler = new Handler();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Set the language for text-to-speech
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("MeditateActivity", "TextToSpeech initialization failed");
                    }
                } else {
                    Log.e("MeditateActivity", "TextToSpeech initialization failed");
                }
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonStart.isChecked()) {
                    if (!is_using_timer) {
                        is_using_timer = true;
                        startTimer();
                        buttonStart.setEnabled(false);
                    } else {
                        showToast("Timer already Running!!");
                    }
                }
                else
                {
                    stopTimer();

                }
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
        long inputTimer=0;
        try
        {
            inputTimer = Long.parseLong(timerValue);
        }
        catch (Exception e)
        {
            showToast(e.getMessage());
            editTextTimer.setText("0");
        }
        if (inputTimer <= 0) {
            showToast("Please enter a valid timer value.");
            return;
        }

        // Update the highest timer if necessary
        if (inputTimer > highestTimer) {
            highestTimer = inputTimer;
            saveHighestTimerToDatabase(highestTimer);
            textViewHighestTimer.setText("Highest Timer: " + highestTimer + " minutes");
        }

        // Start the countdown timer
        timerDuration = inputTimer * 1000 * 60;
        announceCountdownWithVoice(5, new Runnable() {
            @Override
            public void run() {
                // Start the countdown timer after the announcement is completed
                countDownTimer = new CountDownTimer(timerDuration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long secondsRemaining = millisUntilFinished / 1000;
                        textViewCountdown.setText("Countdown: " + secondsRemaining + " seconds");

                        // Calculate the progress percentage
                        int progress = (int) (((float) (timerDuration - millisUntilFinished) / timerDuration) * 100);
                        circularProgressBar.setProgress(progress);
                    }

                    @Override
                    public void onFinish() {
                        textViewCountdown.setText("Countdown: 0 seconds");
                        textToSpeech.speak("Timer Finished", TextToSpeech.QUEUE_ADD, null);
                        showToast("Timer Finished!");
                        circularProgressBar.setProgress(100);
                        is_using_timer=false;
                    }
                };
                buttonStart.setEnabled(true);
                countDownTimer.start();
            }
        });
    }

    private void announceCountdownWithVoice(long inputTimer, final Runnable callback) {
        textToSpeech.speak("Start meditation in " + inputTimer + " seconds", TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (long i = inputTimer; i >= 0; i--) {
                    final long countdownValue = i;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (countdownValue > 0) {
                                textToSpeech.speak(String.valueOf(countdownValue), TextToSpeech.QUEUE_ADD, null);
                            }
                            // Execute the callback after the announcement is completed
                            else  {
                                callback.run();
                            }
                        }
                    }, (inputTimer - countdownValue) * 1000);
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }


    private void startGuidedMeditation() {
        Intent intent = new Intent(this, GuidedMeditate.class);
        startActivity(intent);
    }
    public interface HighestTimerCallback {
        void onHighestTimerFetched(long highestTimer);
    }

    private void fetchHighestTimerFromDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("timers");
        databaseRef.child("highestTimer").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.d("MeditateActivity", "onComplete: Highest timer fetched");
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Long highestTimer = dataSnapshot.getValue(Long.class);
                        if (highestTimer != null) {
                            updateHighestTimerUI(highestTimer);
                        }
                    }
                }
            }
        });
    }
    private void updateHighestTimerUI(long highestTimer) {
        this.highestTimer = highestTimer;
        textViewHighestTimer.setText("Highest Timer: " + highestTimer + " minutes");
    }




    private void saveHighestTimerToDatabase(long timer) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("timers");
        databaseRef.child("highestTimer").setValue(timer);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            is_using_timer = false;
            textViewCountdown.setText("Countdown: 0 seconds");
            circularProgressBar.setProgress(0);
            textToSpeech.stop();

        }
    }

}
