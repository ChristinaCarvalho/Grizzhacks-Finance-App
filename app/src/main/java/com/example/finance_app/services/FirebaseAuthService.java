package com.example.finance_app.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {

  private static final String TAG = "FirebaseAuthService";
  private FirebaseAuth mAuth;

  public FirebaseAuthService() {
    mAuth = FirebaseAuth.getInstance();
  }

  /**
   * Register a new user with email and password
   */
  public void registerUser(String email, String password, AuthCallback callback) {
    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          Log.d(TAG, "User registered successfully");
          callback.onSuccess(mAuth.getCurrentUser());
        } else {
          Log.e(TAG, "Registration failed", task.getException());
          callback.onError(task.getException().getMessage());
        }
      });
  }

  /**
   * Login user with email and password
   */
  public void loginUser(String email, String password, AuthCallback callback) {
    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          Log.d(TAG, "User logged in successfully");
          callback.onSuccess(mAuth.getCurrentUser());
        } else {
          Log.e(TAG, "Login failed", task.getException());
          callback.onError(task.getException().getMessage());
        }
      });
  }

  /**
   * Logout current user
   */
  public void logoutUser() {
    mAuth.signOut();
    Log.d(TAG, "User logged out");
  }

  /**
   * Get current user
   */
  public FirebaseUser getCurrentUser() {
    return mAuth.getCurrentUser();
  }

  /**
   * Check if user is logged in
   */
  public boolean isUserLoggedIn() {
    return mAuth.getCurrentUser() != null;
  }

  /**
   * Reset password
   */
  public void resetPassword(String email, PasswordResetCallback callback) {
    mAuth.sendPasswordResetEmail(email)
      .addOnSuccessListener(aVoid -> {
        Log.d(TAG, "Password reset email sent");
        callback.onSuccess();
      })
      .addOnFailureListener(e -> {
        Log.e(TAG, "Error sending password reset email", e);
        callback.onError(e.getMessage());
      });
  }

  // Callbacks
  public interface AuthCallback {
    void onSuccess(FirebaseUser user);
    void onError(String errorMessage);
  }

  public interface PasswordResetCallback {
    void onSuccess();
    void onError(String errorMessage);
  }
}
