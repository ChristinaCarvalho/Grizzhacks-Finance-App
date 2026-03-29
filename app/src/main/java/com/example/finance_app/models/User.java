package com.example.finance_app.models;

public class User {

  public String userId;
  public String email;
  public String displayName;
  public long createdAt;

  // Default constructor required for Firebase
  public User() {
  }

  public User(String userId, String email, String displayName) {
    this.userId = userId;
    this.email = email;
    this.displayName = displayName;
    this.createdAt = System.currentTimeMillis();
  }

  // Getters and Setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }
}
