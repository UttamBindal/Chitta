package com.example.mentalhealth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextNewUsername;
    private EditText editTextNewPassword;
    private Button buttonSignUp;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        databaseHelper = new DatabaseHelper(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    showToast("Please enter a new username and password.");
                } else {
                    // Check if the new username already exists in the database
                    if (databaseHelper.isUserExists(newUsername)) {
                        showToast("Username already exists. Please choose a different one.");
                    } else {
                        // Add the new user to the database
                        databaseHelper.addUser(newUsername, newPassword);
                        showToast("User created successfully. Please log in.");
                        finish(); // Finish the activity and go back to the login page
                    }
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
