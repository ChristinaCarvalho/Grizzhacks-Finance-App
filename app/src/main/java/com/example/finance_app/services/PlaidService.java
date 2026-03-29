package com.example.finance_app.services;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finance_app.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaidService {

  private static final String TAG = "PlaidService";
  private RequestQueue requestQueue;

  public PlaidService() {
  }

  /**
   * Get Plaid link token from your backend server
   */
  public void getLinktokenFromBackend(LinkTokenCallback callback) {
    try {
      JSONObject jsonBody = new JSONObject();
      // For Cloud Functions, we'll pass user_id as a query param or in body
      // Adjusting to match the index.js logic
      jsonBody.put("user_id", "test-user");

      // Use the specific function URL from Constants
      String url = Constants.PLAID_CREATE_LINK_TOKEN_URL;

      JsonObjectRequest request = new JsonObjectRequest(
        Request.Method.POST,
        url,
        jsonBody,
        response -> {
          try {
            String linkToken = response.getString("link_token");
            Log.d(TAG, "Link token received successfully");
            callback.onLinkTokenReceived(linkToken);
          } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            callback.onLinkTokenReceived(null);
          }
        },
        error -> {
          Log.e(TAG, "Error getting link token: " + (error.networkResponse != null ? error.networkResponse.statusCode : "unknown"), error);
          callback.onLinkTokenReceived(null);
        }
      );

      if (requestQueue == null) {
        requestQueue = Volley.newRequestQueue(null);
      }
      requestQueue.add(request);

    } catch (JSONException e) {
      Log.e(TAG, "Error creating request body", e);
      callback.onLinkTokenReceived(null);
    }
  }

  /**
   * Exchange public token for access token on your backend
   */
  public void exchangePublicToken(String publicToken, ExchangeTokenCallback callback) {
    try {
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("public_token", publicToken);

      // Use the specific function URL from Constants
      String url = Constants.PLAID_EXCHANGE_TOKEN_URL;

      JsonObjectRequest request = new JsonObjectRequest(
        Request.Method.POST,
        url,
        jsonBody,
        response -> {
          try {
            String accessToken = response.getString("access_token");
            String itemId = response.getString("item_id");
            Log.d(TAG, "Token exchanged successfully");
            callback.onTokenExchanged(accessToken, itemId);
          } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            callback.onTokenExchanged(null, null);
          }
        },
        error -> {
          Log.e(TAG, "Error exchanging token", error);
          callback.onTokenExchanged(null, null);
        }
      );

      if (requestQueue == null) {
        requestQueue = Volley.newRequestQueue(null);
      }
      requestQueue.add(request);

    } catch (JSONException e) {
      Log.e(TAG, "Error creating request body", e);
      callback.onTokenExchanged(null, null);
    }
  }

  // Callbacks
  public interface LinkTokenCallback {
    void onLinkTokenReceived(String linkToken);
  }

  public interface ExchangeTokenCallback {
    void onTokenExchanged(String accessToken, String itemId);
  }
}
