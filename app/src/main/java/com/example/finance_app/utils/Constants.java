package com.example.finance_app.utils;


public class Constants {

  // Cloud Function URLs (v2)
  public static final String PLAID_CREATE_LINK_TOKEN_URL = "https://createlinktoken-htqwllmqnq-uc.a.run.app";
  public static final String PLAID_EXCHANGE_TOKEN_URL = "https://exchangetoken-htqwllmqnq-uc.a.run.app";

  public static final String FIREBASE_DATABASE_URL = "https://finance-app-668e2-default-rtdb.firebaseio.com";

  // Plaid settings
  public static final String PLAID_CLIENT_ID = "69c860f1dce309000dfb47e9";
  public static final String PLAID_ENV = "sandbox"; // Options: "sandbox", "development", "production"
  public static final String PLAID_APP_NAME = "Finance App";
  public static final String[] PLAID_SUPPORTED_CONTRIES = {"US"};
  public static final String PLAID_LINK_LANGUAGE = "en";

  // Firebase Database Node Keys
  public static final String FIREBASE_PLAID_DATA_NODE = "plaidData";
  public static final String FIREBASE_USERS_NODE = "users";
  public static final String FIREBASE_ACCOUNTS_NODE = "accounts";
  public static final String FIREBASE_TRANSACTIONS_NODE = "transactions";

  // Request settings
  public static final int REQUEST_TIMEOUT = 30000;

  // Shared Preferences Keys
  public static final String SHARED_PREFS_NAME = "MyAppPrefs";
  public static final String PREF_USER_ID = "user_id";
  public static final String PREF_USER_EMAIL = "user_email";
  public static final String PREF_LINK_TOKEN = "link_token";
  public static final String PREF_LAST_LOGIN = "last_login";

  // Log tags
  public static final String LOG_TAG = "FinanceApp";
  public static final String LOG_TAG_FIREBASE = "Firebase";
  public static final String LOG_TAG_PLAID = "Plaid";
  public static final boolean DEBUG = true;
}
