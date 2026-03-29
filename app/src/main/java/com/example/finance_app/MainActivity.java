package com.example.finance_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finance_app.activities.LoginActivity;
import com.example.finance_app.activities.PlaidLink;
import com.example.finance_app.adapters.AccountsAdapter;
import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.models.PlaidAccountData;
import com.example.finance_app.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseManager firebaseManager;
  private FirebaseAuthService authService;

  private Button linkBankButton;
  private Button logoutButton;
  private ListView accountsListView;
  private ProgressBar loadingProgressBar;
  private AccountsAdapter adapter;
  private List<PlaidAccountData> accountsList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dashboard);

    // Initialize Firebase
    mAuth = FirebaseAuth.getInstance();
    firebaseManager = new FirebaseManager();
    authService = new FirebaseAuthService();

    // Initialize UI components
    linkBankButton = findViewById(R.id.link_bank_button);
    logoutButton = findViewById(R.id.logout_button);
    accountsListView = findViewById(R.id.accounts_list_view);
    loadingProgressBar = findViewById(R.id.loading_progress_bar);

    // Initialize accounts list and adapter
    accountsList = new ArrayList<>();
    adapter = new AccountsAdapter(this, accountsList);
    accountsListView.setAdapter(adapter);

    // Check if user is logged in
    if (mAuth.getCurrentUser() == null) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    } else {
      loadUserAccounts();
    }

    // Set up button listeners
    linkBankButton.setOnClickListener(v -> openPlaidLink());
    logoutButton.setOnClickListener(v -> logoutUser());
  }

  private void openPlaidLink() {
    Intent intent = new Intent(this, PlaidLink.class);
    startActivity(intent);
  }

  private void loadUserAccounts() {
    loadingProgressBar.setVisibility(android.view.View.VISIBLE);
    String userId = mAuth.getCurrentUser().getUid();

    firebaseManager.getUserPlaidAccounts(userId, accounts -> {
      loadingProgressBar.setVisibility(android.view.View.GONE);
      accountsList.clear();
      accountsList.addAll(accounts);
      adapter.notifyDataSetChanged();
    }, error -> {
      loadingProgressBar.setVisibility(android.view.View.GONE);
      Toast.makeText(this, "Error loading accounts: " + error, Toast.LENGTH_SHORT).show();
    });
  }

  private void logoutUser() {
    FirebaseAuth.getInstance().signOut();
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Refresh accounts list when returning to this activity
    loadUserAccounts();
  }
}
