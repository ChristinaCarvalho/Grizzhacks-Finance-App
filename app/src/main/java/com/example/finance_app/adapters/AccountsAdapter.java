package com.example.finance_app.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finance_app.R;
import com.example.finance_app.activities.ProgressActivity;
import com.example.finance_app.database.FirebaseManager;
import com.example.finance_app.models.PlaidAccountData;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountsAdapter extends ArrayAdapter<PlaidAccountData> {

  private Context context;
  private List<PlaidAccountData> accounts;
  private FirebaseManager firebaseManager;
  private FirebaseAuth mAuth;

  public AccountsAdapter(Context context, List<PlaidAccountData> accounts) {
    super(context, 0, accounts);
    this.context = context;
    this.accounts = accounts;
    this.firebaseManager = new FirebaseManager();
    this.mAuth = FirebaseAuth.getInstance();
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(context)
        .inflate(R.layout.item_account, parent, false);
    }

    PlaidAccountData account = accounts.get(position);

    // Set institution name
    TextView institutionName = convertView.findViewById(R.id.institution_name);
    institutionName.setText(account.getInstitutionName() != null ?
      account.getInstitutionName() : "Unknown Bank");

    // Set account name
    TextView accountName = convertView.findViewById(R.id.account_name);
    accountName.setText(account.getAccountName() != null ?
      account.getAccountName() : "Account");

    // Set account type
    TextView accountType = convertView.findViewById(R.id.account_type);
    String typeText = "Type: " + (account.getAccountType() != null ?
      account.getAccountType().toUpperCase() : "UNKNOWN");
    accountType.setText(typeText);

    // Set date linked
    TextView dateLinked = convertView.findViewById(R.id.date_linked);
    String formattedDate = formatDate(account.getTimestamp());
    dateLinked.setText("Linked on: " + formattedDate);

    // Delete button
    Button deleteButton = convertView.findViewById(R.id.delete_button);
    deleteButton.setOnClickListener(v -> deleteAccount(position, account));

    // Handle item click to open ProgressActivity
    convertView.setOnClickListener(v -> {
        Intent intent = new Intent(context, ProgressActivity.class);
        intent.putExtra("ACCOUNT_ID", account.getAccountId() != null ? account.getAccountId() : account.getPublicToken());
        context.startActivity(intent);
    });

    return convertView;
  }

  private void deleteAccount(int position, PlaidAccountData account) {
    String userId = mAuth.getCurrentUser().getUid();
    String accountId = account.getAccountId() != null ?
      account.getAccountId() : account.getPublicToken();

    firebaseManager.deletePlaidAccount(
      userId,
      accountId,
      () -> {
        accounts.remove(position);
        notifyDataSetChanged();
        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show();
      },
      error -> {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
      }
    );
  }

  private String formatDate(long timestamp) {
    try {
      Date date = new Date(timestamp);
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
      return sdf.format(date);
    } catch (Exception e) {
      return "Unknown";
    }
  }
}
