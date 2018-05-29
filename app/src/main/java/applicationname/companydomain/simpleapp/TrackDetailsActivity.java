package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.TextView;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Tracks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RetrofitError;

public class TrackDetailsActivity extends AppCompatActivity {

    private WebView myWebView;

    private String track_id = "";
    private String track_title = "";
    private String track_artist = "";


    public void onRelatedTracksClicked(View v) {
        Intent intent = new Intent(this, RelatedTracks.class);
        intent.putExtra("track_id", track_id);
        intent.putExtra("track_title", track_title);
        intent.putExtra("track_artist", track_artist);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        myWebView = (WebView) findViewById(R.id.webView2);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebChromeClient(new WebChromeClient());

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String track_id = args.getString("track_id", "");
            String track_title = args.getString("track_title", "");
            String track_artist = args.getString("track_artist", "");
            Integer top_track = args.getInt("top_track", -1);

            TextView textView = (TextView) findViewById(R.id.topTrackRank);
            textView.setText("#" + top_track + " Track");

            myWebView = (WebView) findViewById(R.id.webView);

            webSettings.setJavaScriptEnabled(true);

            myWebView.setWebChromeClient(new WebChromeClient());

            String html = "<body style='margin:0;padding:0;'><iframe src=\"https://open.spotify.com/embed/track/" + track_id + "\" width=\"100%\" height=\"355\" frameborder=\"0\" allowtransparency=\"true\"></iframe></body>";
            myWebView.loadData(html, "text/html", null);



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.destroy();
    }
}