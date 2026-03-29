package com.example.finance_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finance_app.MainActivity;
import com.example.finance_app.R;
import com.example.finance_app.services.FirebaseAuthService;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button registerButton;
    private FirebaseAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        authService = new FirebaseAuthService();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            authService.registerUser(email, password, new FirebaseAuthService.AuthCallback() {
                @Override
                public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(RegistrationActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
