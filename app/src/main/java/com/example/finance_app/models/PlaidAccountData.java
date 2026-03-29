package com.example.finance_app.models;

import java.io.Serializable;

public class PlaidAccountData implements Serializable {

  public String publicToken;
  public String accountId;
  public String accountName;
  public String accountType;
  public String institutionName;
  public long timestamp;
  public boolean isActive;
  public double balance;
  public double savingsGoal;

  // New fields for progress
  public double currentAmount;
  public double goalAmount;

  // Default constructor required for Firebase
  public PlaidAccountData() {
  }

  public PlaidAccountData(String publicToken, String accountId, String accountName,
                          String accountType, String institutionName) {
    this.publicToken = publicToken;
    this.accountId = accountId;
    this.accountName = accountName;
    this.accountType = accountType;
    this.institutionName = institutionName;
    this.timestamp = System.currentTimeMillis();
    this.isActive = true;
  }

  // Getters and Setters
  public String getPublicToken() { return publicToken; }
  public void setPublicToken(String publicToken) { this.publicToken = publicToken; }

  public String getAccountId() { return accountId; }
  public void setAccountId(String accountId) { this.accountId = accountId; }

  public String getAccountName() { return accountName; }
  public void setAccountName(String accountName) { this.accountName = accountName; }

  public String getAccountType() { return accountType; }
  public void setAccountType(String accountType) { this.accountType = accountType; }

  public String getInstitutionName() { return institutionName; }
  public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

  public long getTimestamp() { return timestamp; }
  public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

  public boolean isActive() { return isActive; }
  public void setActive(boolean active) { isActive = active; }

  public double getCurrentAmount() { return currentAmount; }
  public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

  public double getGoalAmount() { return goalAmount; }
  public void setGoalAmount(double goalAmount) { this.goalAmount = goalAmount; }

  public int getProgressPercentage() {
    if (goalAmount <= 0) return 0;
    return (int) Math.min(100, (currentAmount / goalAmount) * 100);
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public double getSavingsGoal() {
    return savingsGoal;
  }

  public void setSavingsGoal(double savingsGoal) {
    this.savingsGoal = savingsGoal;
  }

  @Override
  public String toString() {
    return "PlaidAccountData{" +
      "accountName='" + accountName + '\'' +
      ", institutionName='" + institutionName + '\'' +
      ", timestamp=" + timestamp +
      '}';
  }
}
