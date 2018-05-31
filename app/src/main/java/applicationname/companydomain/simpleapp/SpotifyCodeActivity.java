package applicationname.companydomain.simpleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyCodeActivity extends AppCompatActivity {
    private int numberOfLoginAttempts = 0;

    public void resetLoginAttempts() {
        numberOfLoginAttempts = 0;
    }

    protected void fetchNewCode(Activity act) {
        // If we fail to log in after 1 retry, redirect to the main page.
        if (numberOfLoginAttempts > 0) {
            logOut(act);
            return;
        }

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(LoginActivity.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, LoginActivity.REDIRECT_URI);
        builder.setScopes(new String[]{"user-top-read", "user-read-private"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(act,
                LoginActivity.REQUEST_CODE, request);

        numberOfLoginAttempts++;
    }

    protected void logOut(Activity act) {
        MainActivity.spotifyApi.setAccessToken("");

        AuthenticationClient.logout(act);

        Intent intent = new Intent(act, LoginActivity.class);
        intent.putExtra("login", true);
        act.startActivity(intent);
        act.finish();
    }

    public void doActivityResult(int requestCode, int resultCode, Intent intent, myCallback callback) {
        if (requestCode == LoginActivity.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                String access_token = response.getAccessToken();
                MainActivity.spotifyApi.setAccessToken(access_token);
                callback.onSuccess();
            }
        }
    }
}