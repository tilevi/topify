package applicationname.companydomain.simpleapp;

/*
    Code reference for the layouts:
    https://stackoverflow.com/questions/43977565/is-it-possible-to-increase-the-height-of-the-line-inside-the-progress-bar-androi
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrackDetailsActivity extends AppCompatActivity {

    private WebView myWebView;
    private String track_id = "";
    private String track_title = "";
    private String track_artist = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            track_id = args.getString("track_id", "");
            track_title = args.getString("track_title", "");
            track_artist = args.getString("track_artist", "");
            int top_track = args.getInt("top_track", -1);

            // Set up the WebView
            myWebView = (WebView) findViewById(R.id.webView1);
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.setWebChromeClient(new WebChromeClient());

            String html = "<body style='margin:0;padding:0;background:#252f41'><iframe src=\"https://open.spotify.com/embed/track/" + track_id + "\" width=\"100%\" height=\"355\" frameborder=\"0\" allowtransparency=\"true\"></iframe></body>";
            myWebView.loadData(html, "text/html", null);

            // Popularity
            float popularity = args.getFloat("popularity", -1);
            int popInt = (int)(popularity);

            ProgressBar popProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
            popProgressBar.setProgress(popInt);

            TextView popView = (TextView) findViewById(R.id.popView);
            popView.setText("Popularity: " + popInt + "%");

            // Danceability
            float dance = args.getFloat("dance", 0);
            int danceInt = (int)(dance * 100);

            ProgressBar danceProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
            danceProgressBar.setProgress(danceInt);

            TextView danceView = (TextView) findViewById(R.id.danceView);
            danceView.setText("Danceability: " + danceInt + "%");

            // Energy
            float energy = args.getFloat("energy", 0);
            int energyInt = (int)(energy * 100);

            ProgressBar energyProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
            energyProgressBar.setProgress(energyInt);

            TextView energyView = (TextView) findViewById(R.id.energyView);
            energyView.setText("Energy: " + energyInt + "%");

            // Happiness
            float happiness = args.getFloat("happiness", 0);
            int happinessInt = (int)(happiness * 100);

            ProgressBar happinessProgressBar = (ProgressBar) findViewById(R.id.progressBar4);
            happinessProgressBar.setProgress(happinessInt);

            TextView happyView = (TextView) findViewById(R.id.happyView);
            happyView.setText("Happiness: " + happinessInt + "%");


            // Top track rank
            TextView topTrackRank = (TextView) findViewById(R.id.topTrackRank);

            if (top_track == -1) {
                topTrackRank.setText("");
            } else {
                topTrackRank.setText("#" + top_track + " Track");
            }

            // Home image button
            ImageView homeButton = (ImageView) findViewById(R.id.homeButton);

            if (top_track == -1) {
                homeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    /*
                        Source:
                        https://stackoverflow.com/questions/26468619/how-to-finish-all-activities-except-main-activity-and-call-another-activity
                     */
                        Intent intent = new Intent(TrackDetailsActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                });
            } else {
                Toolbar homeBar = (Toolbar) findViewById(R.id.homeBar);
                homeBar.setVisibility(View.GONE);
                homeButton.setVisibility(View.GONE);
            }

        }
    }

    public void onRelatedTracksClicked(View v) {
        Intent intent = new Intent(this, RelatedTracks.class);
        intent.putExtra("track_id", track_id);
        intent.putExtra("track_name", track_title);
        intent.putExtra("track_artist", track_artist);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.destroy();
    }
}