package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TrackDetailsActivity extends AppCompatActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String track_id = args.getString("track_id", "");
            String track_title = args.getString("track_title", "");
            String track_artist = args.getString("track_artist", "");

            myWebView = (WebView) findViewById(R.id.webView);

            WebSettings webSettings = myWebView.getSettings();
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