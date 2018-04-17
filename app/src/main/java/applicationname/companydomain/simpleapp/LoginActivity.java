package applicationname.companydomain.simpleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.Pager;

import retrofit.RetrofitError;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.ResponseCallback;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import android.net.Uri;

public class LoginActivity extends Activity
{
    protected static final String CLIENT_ID = "af04543fa0d549ed902966dc590fe7f1";
    protected static final String REDIRECT_URI = "simpleapp://callback";
    protected static final int REQUEST_CODE = 1337;

    static TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Create a new token manager
        tokenManager = new TokenManager(LoginActivity.this);

        Bundle args = getIntent().getExtras();

        if (args != null && args.getBoolean("login", false)) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(LoginActivity.CLIENT_ID,
                    AuthenticationResponse.Type.CODE, LoginActivity.REDIRECT_URI);
            builder.setScopes(new String[]{"user-top-read", "user-read-private"});
            builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(LoginActivity.this, LoginActivity.REQUEST_CODE, request);
        } else {
            // Grab the access token
            final String token = tokenManager.getToken();

            // Check if the token is not null
            if (token != null) {

                Log.d("onCreate", "The token is not null");

                // If it's not null, then launch the main activity
                Intent intent2 = new Intent(this, MainActivity.class);
                intent2.putExtra("ACCESS_TOKEN", token);
                startActivity(intent2);
                finish();
            }
        }
    }

    public void onLoginButtonClicked(View v) {
        // Grab the access token
        final String token = tokenManager.getToken();

        // Check if the token is null
        if (token == null) {
            // If so, open an authentication window.
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.CODE, REDIRECT_URI);
            builder.setScopes(new String[]{"user-top-read", "user-read-private"});
            //builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        } else {

            // Otherwise, we already have our access token.
            Log.d("AlreadyLoggedIn", token);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ACCESS_TOKEN", token);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LoginActivity.REQUEST_CODE) {
            Log.d("onActivityResult", "RECEIVED REQUEST CODE");

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.CODE) {
                try {
                    // We pass in our authentication code
                    LoginActivity.tokenManager.getAccessToken(response.getCode(), new myCallback() {
                        @Override
                        public void onSuccess(String a_token, String r_token) {
                            // no errors
                            Log.d("onSuccess access token", a_token);
                            Log.d("onSuccess refresh token", r_token);

                            LoginActivity.tokenManager.setTokens(a_token, r_token);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ACCESS_TOKEN", a_token);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String err) {
                            Log.d("onError", "Failed to retrieve new access token.");
                        }
                    });
                } catch (Exception e) {
                    // Exception handler
                }
            }
        }
    }

}