package com.example.finance_app.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finance_app.R;
import com.example.finance_app.adapters.RewardsAdapter;
import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.models.PlaidAccountData;
import com.example.finance_app.models.Reward;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class ProgressActivity extends AppCompatActivity implements RewardsAdapter.OnRewardRedeemedListener {

    private TextView institutionNameDisplay;
    private TextView amountDisplay;
    private TextView goalDisplay;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button addGoalButton;
    private ImageButton btnAddReward;
    private Button homeButton;
    private ListView rewardsListView;
    private RewardsAdapter rewardsAdapter;

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private String accountId;
    private PlaidAccountData currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);

        firebaseManager = new FirebaseManager();
        mAuth = FirebaseAuth.getInstance();

        // Get account ID from intent
        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        initViews();
        loadAccountData();
        loadRewards();

        homeButton.setOnClickListener(v -> finish());

        addGoalButton.setOnClickListener(v -> showUpdateGoalDialog());

        btnAddReward.setOnClickListener(v -> showAddRewardDialog());
    }

    private void initViews() {
        institutionNameDisplay = findViewById(R.id.institution_name_display);
        amountDisplay = findViewById(R.id.Amount);
        goalDisplay = findViewById(R.id.SavingGoal);
        progressBar = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentageText);
        addGoalButton = findViewById(R.id.addGoal);
        btnAddReward = findViewById(R.id.btnAddReward);
        homeButton = findViewById(R.id.home);
        rewardsListView = findViewById(R.id.rewards);

        // Initial goal is 0, will be updated when account data loads
        rewardsAdapter = new RewardsAdapter(this, new ArrayList<>(), 0.0, this);
        rewardsListView.setAdapter(rewardsAdapter);

        // Ensure addGoal button has correct text
        addGoalButton.setText(R.string.add_goal);
    }

    private void showUpdateGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Savings Goal");

        final EditText input = new EditText(this);
        input.setHint("Enter new goal amount");
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Pre-fill with current goal if available
        if (currentAccount != null) {
            input.setText(String.valueOf(currentAccount.getGoalAmount()));
        }

        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String goalStr = input.getText().toString().trim();
            if (!goalStr.isEmpty()) {
                try {
                    double newGoal = Double.parseDouble(goalStr);
                    saveSavingsGoalToFirebase(newGoal);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveSavingsGoalToFirebase(double newGoal) {
        if (mAuth.getCurrentUser() == null || accountId == null) return;

        firebaseManager.updateSavingsGoal(mAuth.getCurrentUser().getUid(), accountId, newGoal,
                () -> Toast.makeText(this, "Savings goal updated!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRewardRedeemed(Reward reward) {
        Toast.makeText(this, "Redeemed: " + reward.getName(), Toast.LENGTH_LONG).show();
        reward.setAchieved(true);
    }

    private void showAddRewardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_reward, null);
        builder.setView(dialogView);

        EditText rewardNameInput = dialogView.findViewById(R.id.rewardName);
        EditText priceInput = dialogView.findViewById(R.id.targetAmount);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = rewardNameInput.getText().toString().trim();
            String amountStr = priceInput.getText().toString().trim();

            if (!name.isEmpty() && !amountStr.isEmpty()) {
                try {
                    double price = Double.parseDouble(amountStr);
                    saveRewardToFirebase(name, price);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void saveRewardToFirebase(String name, double price) {
        if (mAuth.getCurrentUser() == null || accountId == null) return;

        Reward newReward = new Reward(null, name, price);
        firebaseManager.addReward(mAuth.getCurrentUser().getUid(), accountId, newReward,
                () -> Toast.makeText(this, "Reward added!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show());
    }

    private void loadAccountData() {
        if (mAuth.getCurrentUser() == null || accountId == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        firebaseManager.getUserPlaidAccounts(userId, accounts -> {
            for (PlaidAccountData account : accounts) {
                if (accountId.equals(account.getAccountId()) || accountId.equals(account.getPublicToken())) {
                    currentAccount = account;
                    updateUI();

                    // Update the adapter with the real savings goal
                    if (rewardsAdapter != null) {
                        rewardsAdapter.updateSavingsGoal(currentAccount.getGoalAmount());
                    }
                    break;
                }
            }
        }, error -> {
            Toast.makeText(this, "Error loading account: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRewards() {
        if (mAuth.getCurrentUser() == null || accountId == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        firebaseManager.getRewards(userId, accountId, rewards -> {
            rewardsAdapter.clear();
            rewardsAdapter.addAll(rewards);
            rewardsAdapter.notifyDataSetChanged();
        }, error -> {
            Toast.makeText(this, "Error loading rewards: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        if (currentAccount == null) return;

        institutionNameDisplay.setText(currentAccount.getInstitutionName());
        amountDisplay.setText(String.format(Locale.US, "Amount: $%.2f", currentAccount.getCurrentAmount()));
        goalDisplay.setText(String.format(Locale.US, "Savings Goal: $%.2f", currentAccount.getGoalAmount()));

        int progress = currentAccount.getProgressPercentage();
        progressBar.setProgress(progress);
        percentageText.setText(String.format(Locale.US, "%d%%", progress));
    }
}
