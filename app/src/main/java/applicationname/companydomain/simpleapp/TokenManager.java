package applicationname.companydomain.simpleapp;

import android.media.session.MediaSession;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    final String ACCESS_TOKEN = "applicationname.companydomain.simpleapp.TOKEN";
    final String ACCESS_TOKEN_KEY = "access_token";
    final String REFRESH_TOKEN_KEY = "refresh_token";

    final SharedPreferences sharedPrefs;

    public TokenManager(Context c) {
        sharedPrefs = c.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE);
                //new SecurePreferences(c,
                        //"kmaskd123jnmajsndja",
                        //"my_user_prefs.xml");
    }

    public void clearToken() {
        Editor editor = sharedPrefs.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.commit();
    }

    public String getToken() {
        return sharedPrefs.getString(ACCESS_TOKEN_KEY,null);
    }

    public String getRefreshToken() {
        return sharedPrefs.getString(REFRESH_TOKEN_KEY,null);
    }
    public void setTokens(String a_token, String r_token) {
       Editor editor = sharedPrefs.edit();
        editor.putString(ACCESS_TOKEN_KEY, a_token); // Store the access token

        // If r_token is null, then don't update it.
        if (r_token != null) {
            editor.putString(REFRESH_TOKEN_KEY, r_token); // Store the refresh token
            Log.d("setTokens r_token", r_token);
        }

        Log.d("setTokens a_token", a_token);

        editor.commit();

        // Set the new access token
        MainActivity.spotifyApi.setAccessToken(a_token);
    }

    void getNewToken(final myCallback callback) {
        String refreshToken = sharedPrefs.getString(REFRESH_TOKEN_KEY,null);

        OkHttpClient client = new OkHttpClient();
        String url = "http://rootify.io/android/" + refreshToken;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("onFailure", "Failed to fetch new access token.");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                try {
                    JSONObject json = new JSONObject(myResponse);

                    String access_token = json.getJSONObject("body").getString("access_token");
                    callback.onSuccess(access_token, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getAccessToken(final String code, final myCallback callback) throws IOException {

        OkHttpClient client = new OkHttpClient();
        String url = "http://rootify.io/android/" + code;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("onFailure", "Failed to fetch access and refresh tokens.");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                try {
                    JSONObject json = new JSONObject(myResponse);

                    String access_token = json.getJSONObject("body").getString("access_token");
                    String refresh_token = json.getJSONObject("body").getString("refresh_token");

                    callback.onSuccess(access_token, refresh_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
