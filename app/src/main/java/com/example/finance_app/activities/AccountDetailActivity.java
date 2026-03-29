package com.example.finance_app.activities;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finance_app.R;
import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.models.PlaidAccountData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class AccountDetailActivity extends AppCompatActivity {

    private TextView accountNameText;
    private TextView amountText;
    private TextView savingGoalText;
    private Button addGoalButton;
    private Button homeButton;

    private PlaidAccountData accountData;
    private FirebaseManager firebaseManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);

        firebaseManager = new FirebaseManager();
        userId = FirebaseAuth.getInstance().getUid();

        accountData = (PlaidAccountData) getIntent().getSerializableExtra("account_data");

        if (accountData == null) {
            Toast.makeText(this, "Error: Account data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        accountNameText = findViewById(R.id.Account);
        amountText = findViewById(R.id.Amount);
        savingGoalText = findViewById(R.id.SavingGoal);
        addGoalButton = findViewById(R.id.addGoal);
        homeButton = findViewById(R.id.home);

        setupUI();

        addGoalButton.setOnClickListener(v -> showAddGoalDialog());
        homeButton.setOnClickListener(v -> finish());
    }

    private void setupUI() {
        accountNameText.setText(accountData.getAccountName());
        amountText.setText(String.format(Locale.getDefault(), "Amount: $%.2f", accountData.getBalance()));
        updateGoalUI();
    }

    private void updateGoalUI() {
        double goal = accountData.getSavingsGoal();
        savingGoalText.setText(String.format(Locale.getDefault(), "Savings Goal: $%.2f", goal));
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Savings Goal");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter amount");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String goalStr = input.getText().toString();
            if (!goalStr.isEmpty()) {
                try {
                    double newGoal = Double.parseDouble(goalStr);
                    saveGoalToFirebase(newGoal);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveGoalToFirebase(double newGoal) {
        accountData.setSavingsGoal(newGoal);
        String accountKey = accountData.getAccountId();
        
        firebaseManager.updatePlaidAccount(userId, accountKey, accountData, 
            () -> {
                Toast.makeText(AccountDetailActivity.this, "Goal updated!", Toast.LENGTH_SHORT).show();
                updateGoalUI();
            }, 
            error -> Toast.makeText(AccountDetailActivity.this, "Failed to update goal: " + error, Toast.LENGTH_SHORT).show()
        );
    }
}
