package com.example.mentalhealth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonTakeTest;
    private Button buttonMeditate;
    private Button buttonTipsAndTricks;
    private Button buttonAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buttonTakeTest = findViewById(R.id.buttonTakeTest);
        buttonMeditate = findViewById(R.id.buttonMeditate);
        buttonTipsAndTricks = findViewById(R.id.buttonTipsAndTricks);
        buttonAbout = findViewById(R.id.buttonAbout);

        buttonTakeTest.setOnClickListener(this);
        buttonMeditate.setOnClickListener(this);
        buttonTipsAndTricks.setOnClickListener(this);
        buttonAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTakeTest) {
            navigateToTakeTestPage();
        } else if (v.getId() == R.id.buttonMeditate) {
            navigateToMeditatePage();
        } else if (v.getId() == R.id.buttonTipsAndTricks) {
            navigateToTipsAndTricksPage();
        } else if (v.getId() == R.id.buttonAbout) {
            navigateToAboutPage();
        }
    }

    private void navigateToTakeTestPage() {
        Intent intent = new Intent(this, TakeTestActivity.class);
        startActivity(intent);
    }

    private void navigateToMeditatePage() {
        Intent intent = new Intent(this, MeditateActivity.class);
        startActivity(intent);
    }

    private void navigateToTipsAndTricksPage() {
        Intent intent = new Intent(this, TipsAndTricksActivity.class);
        startActivity(intent);
    }

    private void navigateToAboutPage() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
