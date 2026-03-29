package com.example.finance_app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.example.finance_app.R;
import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.services.PlaidService;
import com.google.firebase.auth.FirebaseAuth;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkActivityResultContract;

public class PlaidLink extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseManager firebaseManager;
  private PlaidService plaidService;
  private ActivityResultLauncher<LinkTokenConfiguration> linkLauncher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_plaid_link);

    mAuth = FirebaseAuth.getInstance();
    firebaseManager = new FirebaseManager();
    plaidService = new PlaidService();

    // Set up Plaid Link result launcher
    linkLauncher = registerForActivityResult(
      new LinkActivityResultContract(),
      result -> {
        if (result.isSuccess()) {
          String publicToken = result.getSuccess().getPublicToken();
          String metadata = result.getSuccess().getMetadata().toString();
          savePlaidDataToFirebase(publicToken, metadata);
        } else if (result.isCancel()) {
          Toast.makeText(this, "Link cancelled", Toast.LENGTH_SHORT).show();
          finish();
        } else if (result.isExit()) {
          Toast.makeText(this, "Link exited", Toast.LENGTH_SHORT).show();
          finish();
        }
      }
    );

    // Start Plaid Link flow
    openPlaidLink();
  }

  private void openPlaidLink() {
    // Get link token from your backend
    plaidService.getLinktokenFromBackend(this, linkToken -> {
      if (linkToken != null) {
        // Correct way to build LinkTokenConfiguration in Plaid Android SDK 5.x
        LinkTokenConfiguration configuration = new LinkTokenConfiguration.Builder()
          .token(linkToken)
          .build();

        linkLauncher.launch(configuration);
      } else {
        Toast.makeText(this, "Failed to get link token", Toast.LENGTH_SHORT).show();
        finish();
      }
    });
  }

  private void savePlaidDataToFirebase(String publicToken, String metadata) {
    if (mAuth.getCurrentUser() == null) return;
    String userId = mAuth.getCurrentUser().getUid();

    firebaseManager.savePlaidAccount(
      userId,
      publicToken,
      metadata,
      () -> {
        Toast.makeText(PlaidLink.this,
          "Bank account linked successfully!", Toast.LENGTH_SHORT).show();
        finish();
      },
      error -> {
        Toast.makeText(PlaidLink.this,
          "Error: " + error, Toast.LENGTH_SHORT).show();
      }
    );
  }
}
