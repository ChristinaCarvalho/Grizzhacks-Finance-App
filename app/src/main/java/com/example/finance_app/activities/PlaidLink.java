package com.example.finance_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.example.finance_app.R;
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
    setContentView(R.layout.activity_plaid_link);

    mAuth = FirebaseAuth.getInstance();
    firebaseManager = new FirebaseManager();
    plaidService = new PlaidService();

    // Set up Plaid Link result launcher using the OpenPlaidLink contract
    linkLauncher = registerForActivityResult(
      new OpenPlaidLink(),
      result -> {
        if (result instanceof LinkSuccess) {
          LinkSuccess success = (LinkSuccess) result;
          String publicToken = success.getPublicToken();

          // Safe null check for metadata
          String metadata = success.getMetadata().toString();

          savePlaidDataToFirebase(publicToken, metadata);
        } else if (result instanceof LinkExit) {
          LinkExit exit = (LinkExit) result;
          if (exit.getError() != null) {
            Log.e(TAG, "Plaid Link error: " + exit.getError().getDisplayMessage());
            showToast("Link error: " + exit.getError().getDisplayMessage());
          } else {
            Log.d(TAG, "Plaid Link cancelled");
            showToast("Link cancelled");
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
    plaidService.getLinktokenFromBackend(this, new PlaidService.LinkTokenCallback() {
      @Override
      public void onLinkTokenReceived(String linkToken) {
        if (linkToken != null) {
          LinkTokenConfiguration configuration = new LinkTokenConfiguration.Builder()
            .token(linkToken)
            .build();

          linkLauncher.launch(configuration);
        } else {
          showToast("Failed to get link token");
          finish();
        }
      }

      @Override
      public void onError(Exception e) {
        Log.e(TAG, "Error getting link token", e);
        showToast("Error: " + e.getMessage());
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
        showToast("Bank account linked successfully!");
        finish();
      },
      error -> {
        Log.e(TAG, "Firebase error: " + error);
        showToast("Error: " + error);
        finish();
      }
    );
  }

  /**
   * Helper method to show Toast messages on main thread
   */
  private void showToast(String message) {
    runOnUiThread(() -> Toast.makeText(PlaidLink.this, message, Toast.LENGTH_SHORT).show());
  }
}
