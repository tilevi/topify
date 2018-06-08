package applicationname.companydomain.simpleapp;

/*
    Sources:

        Spotify Android SDK:
                https://developer.spotify.com/documentation/android-sdk/

        spotify-web-api-android:
                https://github.com/kaaes/spotify-web-api-android

        Spotify Web API:
                https://developer.spotify.com/documentation/web-api/
 */

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
            // showLoginPage(false);

            Intent loginToMainIntent = new Intent(LoginActivity.this,
                    MainActivity.class);
            loginToMainIntent.putExtra("ACCESS_TOKEN", "");
            startActivity(loginToMainIntent);
            finish();
        }
    }

    private void showLoginPage(boolean showDialog) {
        // Open an authentication window.
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
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
                case TOKEN:
                    // Set that we just logged in.
                    Editor editor = sharedPrefs.edit();
                    editor.putBoolean(LOGGED_IN, true);
                    editor.apply();

                    // Switch to MainActivity.
                    Intent loginToMainIntent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    loginToMainIntent.putExtra("ACCESS_TOKEN", response.getAccessToken());
                    startActivity(loginToMainIntent);
                    finish();
            }
        }
    }
}