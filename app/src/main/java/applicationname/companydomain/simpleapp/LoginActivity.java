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
    protected static final String CLIENT_ID = "***REMOVED***";
    protected static final String REDIRECT_URI = "simpleapp://callback";
    protected static final int REQUEST_CODE = 1337;

    private SharedPreferences sharedPrefs;
    final String ACCESS_TOKEN = "applicationname.companydomain.simpleapp.TOKEN";
    final String LOGGED_IN = "logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        sharedPrefs = this.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPrefs.getBoolean(LOGGED_IN,false);

        Bundle args = getIntent().getExtras();
        if (args != null && args.getBoolean("login", false)) {
            Log.d("TestLogin", "LOGGED OUT");

            // We just logged out.
            Editor editor = sharedPrefs.edit();
            editor.putBoolean(LOGGED_IN, false);
            editor.apply();

            // Call logout() again to be safe.
            AuthenticationClient.logout(LoginActivity.this);
        } else if (loggedIn) {
            // If we're logged in, then attempt to log in.
            showLoginPage(false);
        }
    }

    private void showLoginPage(boolean showDialog) {
        // Open an authentication window.
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.CODE, REDIRECT_URI);
        builder.setScopes(new String[]{"user-top-read", "user-read-private"});
        builder.setShowDialog(showDialog);
        AuthenticationRequest request = builder.build();

        // AuthenticationClient.openLoginInBrowser(this, request);
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void onLoginButtonClicked(View v) {
        // This is just to make sure that no one is logged in.
        AuthenticationClient.logout(LoginActivity.this);
        // Show the Spotify login page.
        showLoginPage(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LoginActivity.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case CODE:
                    // Set that we just logged in.
                    Editor editor = sharedPrefs.edit();
                    editor.putBoolean(LOGGED_IN, true);
                    editor.apply();

                    // Switch to MainActivity.
                    Intent loginToMainIntent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    loginToMainIntent.putExtra("ACCESS_TOKEN", response.getCode());
                    startActivity(loginToMainIntent);
                    finish();
            }
        }
    }
}