package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class ArtistDetailsActivity extends SpotifyCodeActivity {
    private TextView artistBio = null;
    private WebView myWebView;

    private ArrayList<String> topTracks = new ArrayList<String>();
    private String artist_id = "";
    private String artist_name = "";

    private void setBioText(final String text, final boolean isNull) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isNull) {
                    TextView aboutView = (TextView) findViewById(R.id.aboutView);
                    aboutView.setVisibility(View.GONE);
                    artistBio.setVisibility(View.GONE);
                } else {
                    artistBio.setText(text);
                }
            }
        });
    }

    private void hideTopTracks() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView trackView = (TextView) findViewById(R.id.trackView);
                trackView.setVisibility(View.GONE);
            }
        });
    }

    public void onRelatedArtistsClicked(View v) {
        Intent intent = new Intent(this, RelatedArtists.class);
        intent.putExtra("artist_id", artist_id);
        intent.putExtra("artist_name", artist_name);
        startActivity(intent);
    }

    private void retrievedTopTracks() {
        if (topTracks.size() <= 0) {
            hideTopTracks();
        } else {
            StringBuilder html = new StringBuilder("");

            html.append("<body style='margin:0;padding:0;'>");

            int i = 0;
            while (i < topTracks.size()) {
                html.append("<iframe src=\"https://open.spotify.com/embed/track/" + topTracks.get(i)
                        + "\" width=\"100%\" height=\"80px\" frameborder=\"0\" allowtransparency=\"true\"></iframe>");
                i++;
            }

            html.append("</body>");
            myWebView.loadData(html.toString(), "text/html", null);
        }

        // Reset the login attempts.
        resetLoginAttempts();
    }

    private void fetchArtistTopTracks() {
        MainActivity.spotify.getArtistTopTrack(artist_id, "US",
                new retrofit.Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, retrofit.client.Response response) {
                        int i = 0;
                        while (i < 2 && i < tracks.tracks.size() && tracks.tracks.get(i) != null) {
                            topTracks.add(tracks.tracks.get(i).id);
                            i++;
                        }
                        retrievedTopTracks();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        fetchNewCode(ArtistDetailsActivity.this);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        myWebView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebChromeClient(new WebChromeClient());

        Bundle args = getIntent().getExtras();

        if (args != null) {
            artist_id = args.getString("artist_id", "");
            artist_name = args.getString("artist_name", "");
            String artist_url = args.getString("artist_url", "");
            Integer top_artist = args.getInt("top_artist", -1);

            float popularity = args.getFloat("popularity", -1);

            Log.d("FoundPop", Float.toString(popularity));

            TextView textView = (TextView) findViewById(R.id.topArtistRank);
            if (top_artist != -1) {
                textView.setText("#" + top_artist + " Artist");
            } else {
                textView.setText("");
            }

            ImageView artistImage = (ImageView) findViewById(R.id.imageView);

            if (!artist_url.equals("")) {
                Glide.with(ArtistDetailsActivity.this)
                        .load(artist_url)
                        .into(artistImage);
            } else {
                Glide.with(ArtistDetailsActivity.this)
                        .load(R.drawable.unknown)
                        .into(artistImage);
            }

            TextView topArtistRank = (TextView) findViewById(R.id.topArtistRank);

            if (top_artist == -1) {
                topArtistRank.setText("");
            } else {
                topArtistRank.setText("#" + top_artist + " Artist");
            }

            int popInt = (int) (popularity);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setProgress(popInt);
            //our progress bars need different colors
            // the res/drawable directory make a new resource
            // that's p much identical to progbarstates.xml
            // onCreate, set the progress, grab the textview
            // Set the rank (#d track) top_artist
            // button top right related tracks
            // right now the button doesnt have to do anything
            // CLEAN THE PROJECT BEFORE PUSH

            TextView popView = (TextView) findViewById(R.id.popView);
            popView.setText("Popularity: " + popInt + "%");

            artistBio = (TextView) findViewById(R.id.artistBio);

            TextView artistName = (TextView) findViewById(R.id.artistName);
            artistName.setText(artist_name);

            // Get the artist's top tracks.
            fetchArtistTopTracks();

            OkHttpClient client = new OkHttpClient();
            String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
                    + artist_name + "&api_key=9f5228be9f2d49c1700e60d8d3e02eb3&format=json";

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String myResponse = response.body().string();

                    try {
                        JSONObject json = new JSONObject(myResponse);
                        boolean isNull = false;

                        if (json != null) {
                            JSONObject jsonArtist = json.getJSONObject("artist");
                            if (jsonArtist != null) {
                                JSONObject jsonBio = jsonArtist.getJSONObject("bio");
                                if (jsonBio != null) {
                                    String summary = jsonBio.getString("summary");
                                    summary = summary.substring(0, (summary.substring(0,
                                            summary.lastIndexOf("<a"))).lastIndexOf(".") + 1);
                                    String summaryString = summary.toString();

                                    if (summaryString.equals("")) {
                                        isNull = true;
                                    }

                                    setBioText(summaryString, isNull);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            artistBio.setText("");

            // Home image button
            ImageView homeButton = (ImageView) findViewById(R.id.homeButton);

            if (top_artist == -1) {
                homeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    /*
                        Source:
                        https://stackoverflow.com/questions/26468619/how-to-finish-all-activities-except-main-activity-and-call-another-activity
                     */
                        Intent intent = new Intent(ArtistDetailsActivity.this, MainActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        doActivityResult(requestCode, resultCode, intent, new myCallback() {
            @Override
            public void onSuccess() {
                fetchArtistTopTracks();
            }
        });
    }
}