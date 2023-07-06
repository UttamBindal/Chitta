package com.example.mentalhealth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.FirebaseApp;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextNewUsername;
    private EditText editTextNewPassword;
    private Button buttonSignUp;

    // Regular expression pattern for email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        FirebaseApp.initializeApp(this);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    showToast("Please enter a new username and password.");
                } else if (!isValidEmail(newUsername)) {
                    showToast("Please enter a valid email address.");
                } else if (!isValidPassword(newPassword)) {
                    showToast("Please enter a valid password. Password must be at least 8 characters long and contain an uppercase letter, a lowercase letter, a number, and a special character.");
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    // Create a new user with email and password
                    auth.createUserWithEmailAndPassword(newUsername, newPassword)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User creation successful
                                        showToast("User created successfully. Please log in.");
                                        finish(); // Finish the activity and go back to the login page
                                    } else {
                                        // User creation failed
                                        showToast("User creation failed. Please try again.");
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain an uppercase letter, a lowercase letter, a number, and a special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
