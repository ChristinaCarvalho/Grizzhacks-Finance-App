package com.example.finance_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.services.PlaidService;
import com.google.firebase.auth.FirebaseAuth;
import com.plaid.link.OpenPlaidLink;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkSuccess;

public class PlaidLink extends AppCompatActivity {

  private static final String TAG = "PlaidLink";
  private FirebaseAuth mAuth;
  private FirebaseManager firebaseManager;
  private PlaidService plaidService;
  private ActivityResultLauncher<LinkTokenConfiguration> linkLauncher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // This activity is a launcher for Plaid Link, it doesn't need its own complex layout
    
    mAuth = FirebaseAuth.getInstance();
    firebaseManager = new FirebaseManager();
    plaidService = new PlaidService();

    // Set up Plaid Link result launcher
    linkLauncher = registerForActivityResult(
      new OpenPlaidLink(),
      result -> {
        if (result instanceof LinkSuccess) {
          LinkSuccess success = (LinkSuccess) result;
          String publicToken = success.getPublicToken();
          String metadata = success.getMetadata().toString();
          savePlaidDataToFirebase(publicToken, metadata);
        } else if (result instanceof LinkExit) {
          LinkExit exit = (LinkExit) result;
          if (exit.getError() != null) {
            Log.e(TAG, "Plaid Link error: " + exit.getError().getDisplayMessage());
            Toast.makeText(this, "Link error: " + exit.getError().getDisplayMessage(), Toast.LENGTH_SHORT).show();
          } else {
            Log.d(TAG, "Plaid Link cancelled");
            Toast.makeText(this, "Link cancelled", Toast.LENGTH_SHORT).show();
          }
          finish();
        } else {
          finish();
        }
      }
    );

    // Start Plaid Link flow
    openPlaidLink();
  }

  private void openPlaidLink() {
    // Get link token from your backend
    plaidService.getLinktokenFromBackend(linkToken -> {
      if (linkToken != null) {
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
    if (mAuth.getCurrentUser() == null) {
      finish();
      return;
    }
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
        finish();
      }
    );
  }
}
