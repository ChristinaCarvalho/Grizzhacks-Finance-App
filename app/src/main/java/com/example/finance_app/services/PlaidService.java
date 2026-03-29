package com.example.finance_app.services;

import android.content.Context;
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
  public void getLinktokenFromBackend(Context context, LinkTokenCallback callback) {
    try {
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("user_id", "test-user");

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
            callback.onError(e);
          }
        },
        error -> {
          Log.e(TAG, "Error getting link token: " + (error.networkResponse != null ? error.networkResponse.statusCode : "unknown"), error);
          callback.onError(error);
        }
      );

      if (requestQueue == null) {
        requestQueue = Volley.newRequestQueue(context);
      }
      requestQueue.add(request);

    } catch (JSONException e) {
      Log.e(TAG, "Error creating request body", e);
      callback.onError(e);
    }
  }

  /**
   * Exchange public token for access token on your backend
   */
  public void exchangePublicToken(Context context, String publicToken, ExchangeTokenCallback callback) {
    try {
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("public_token", publicToken);

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
            callback.onError(e);
          }
        },
        error -> {
          Log.e(TAG, "Error exchanging token", error);
          callback.onError(error);
        }
      );

      if (requestQueue == null) {
        requestQueue = Volley.newRequestQueue(context);
      }
      requestQueue.add(request);

    } catch (JSONException e) {
      Log.e(TAG, "Error creating request body", e);
      callback.onError(e);
    }
  }

  // Callbacks
  public interface LinkTokenCallback {
    void onLinkTokenReceived(String linkToken);
    void onError(Exception e);
  }

  public interface ExchangeTokenCallback {
    void onTokenExchanged(String accessToken, String itemId);
    void onError(Exception e);
  }
}
