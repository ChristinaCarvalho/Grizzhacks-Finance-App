package com.example.finance_app.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.finance_app.models.PlaidAccountData;
import com.example.finance_app.models.Reward;
import com.example.finance_app.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {

  private static final String TAG = "FirebaseManager";
  private DatabaseReference mDatabase;

  public FirebaseManager() {
    mDatabase = FirebaseDatabase.getInstance().getReference();
  }

  /**
   * Save Plaid account data to Firebase
   */
  public void savePlaidAccount(String userId, String publicToken, String metadata,
                               OnSuccessListener successListener,
                               OnErrorListener errorListener) {
    String accountId = mDatabase.push().getKey();

    PlaidAccountData accountData = new PlaidAccountData();
    accountData.setPublicToken(publicToken);
    accountData.setAccountId(accountId);
    accountData.setTimestamp(System.currentTimeMillis());

    mDatabase.child("plaidData")
      .child(userId)
      .child(accountId)
      .setValue(accountData)
      .addOnSuccessListener(aVoid -> {
        Log.d(TAG, "Account saved successfully");
        if (successListener != null) {
          successListener.onSuccess();
        }
      })
      .addOnFailureListener(e -> {
        Log.e(TAG, "Error saving account", e);
        if (errorListener != null) {
          errorListener.onError(e.getMessage());
        }
      });
  }

  /**
   * Retrieve all Plaid accounts for a user
   */
  public void getUserPlaidAccounts(String userId, OnAccountsLoadedListener listener,
                                   OnErrorListener errorListener) {
    mDatabase.child("plaidData")
      .child(userId)
      .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          List<PlaidAccountData> accounts = new ArrayList<>();

          for (DataSnapshot accountSnapshot : snapshot.getChildren()) {
            PlaidAccountData account = accountSnapshot.getValue(PlaidAccountData.class);
            if (account != null) {
              // Ensure accountId is set from the database key if it's missing in the object
              if (account.getAccountId() == null) {
                account.setAccountId(accountSnapshot.getKey());
              }
              accounts.add(account);
            }
          }

          if (listener != null) {
            listener.onAccountsLoaded(accounts);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e(TAG, "Error loading accounts", error.toException());
          if (errorListener != null) {
            errorListener.onError(error.getMessage());
          }
        }
      });
  }

  /**
   * Delete a Plaid account
   */
  public void deletePlaidAccount(String userId, String accountId,
                                 OnSuccessListener successListener,
                                 OnErrorListener errorListener) {
    if (accountId == null) {
      if (errorListener != null) {
        errorListener.onError("Account ID is null");
      }
      return;
    }

    mDatabase.child("plaidData")
      .child(userId)
      .child(accountId)
      .removeValue()
      .addOnSuccessListener(aVoid -> {
        Log.d(TAG, "Account deleted successfully");
        if (successListener != null) {
          successListener.onSuccess();
        }
      })
      .addOnFailureListener(e -> {
        Log.e(TAG, "Error deleting account", e);
        if (errorListener != null) {
          errorListener.onError(e.getMessage());
        }
      });
  }

  /**
   * Update a Plaid account
   */
  public void updatePlaidAccount(String userId, String accountId, PlaidAccountData updatedData,
                                 OnSuccessListener successListener,
                                 OnErrorListener errorListener) {
    mDatabase.child("plaidData")
      .child(userId)
      .child(accountId)
      .setValue(updatedData)
      .addOnSuccessListener(aVoid -> {
        Log.d(TAG, "Account updated successfully");
        if (successListener != null) {
          successListener.onSuccess();
        }
      })
      .addOnFailureListener(e -> {
        Log.e(TAG, "Error updating account", e);
        if (errorListener != null) {
          errorListener.onError(e.getMessage());
        }
      });
  }

  /**
   * Add a reward to an account
   */
  public void addReward(String userId, String accountId, Reward reward,
                        OnSuccessListener successListener,
                        OnErrorListener errorListener) {
    DatabaseReference rewardRef = mDatabase.child("plaidData")
            .child(userId)
            .child(accountId)
            .child("rewards")
            .push();

    reward.setId(rewardRef.getKey());

    rewardRef.setValue(reward)
      .addOnSuccessListener(aVoid -> {
        if (successListener != null) successListener.onSuccess();
      })
      .addOnFailureListener(e -> {
        if (errorListener != null) errorListener.onError(e.getMessage());
      });
  }

  /**
   * Get all rewards for an account
   */
  public void getRewards(String userId, String accountId, OnRewardsLoadedListener listener,
                         OnErrorListener errorListener) {
    mDatabase.child("plaidData")
      .child(userId)
      .child(accountId)
      .child("rewards")
      .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          List<Reward> rewards = new ArrayList<>();
          for (DataSnapshot rewardSnapshot : snapshot.getChildren()) {
            Reward reward = rewardSnapshot.getValue(Reward.class);
            if (reward != null) {
              rewards.add(reward);
            }
          }
          if (listener != null) listener.onRewardsLoaded(rewards);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          if (errorListener != null) errorListener.onError(error.getMessage());
        }
      });
  }

  /**
   * Save user profile data
   */
  public void saveUserProfile(String userId, String email, String displayName,
                              OnSuccessListener successListener,
                              OnErrorListener errorListener) {
    mDatabase.child("users")
      .child(userId)
      .child("profile")
      .setValue(new User(userId, email, displayName))
      .addOnSuccessListener(aVoid -> {
        if (successListener != null) {
          successListener.onSuccess();
        }
      })
      .addOnFailureListener(e -> {
        if (errorListener != null) {
          errorListener.onError(e.getMessage());
        }
      });
  }

  // Listener interfaces
  public interface OnSuccessListener {
    void onSuccess();
  }

  public interface OnErrorListener {
    void onError(String errorMessage);
  }

  public interface OnAccountsLoadedListener {
    void onAccountsLoaded(List<PlaidAccountData> accounts);
  }

  public interface OnRewardsLoadedListener {
    void onRewardsLoaded(List<Reward> rewards);
  }
}
