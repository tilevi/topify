package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Tracks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RetrofitError;

public class ArtistDetailsActivity extends AppCompatActivity {
    private TextView artistBio = null;
    private WebView myWebView;

    private ArrayList<String> topTracks = new ArrayList<String>();
    private String artist_id = "";
    private String artist_name = "";

    private void setBioText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                artistBio.setText(text);
                Log.d("ArtistBio", "set text");
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

            Glide.with(ArtistDetailsActivity.this)
                    .load(artist_url)
                    .into(artistImage);

            TextView topArtistRank = (TextView) findViewById(R.id.topArtistRank);

            if (top_artist == -1) {
                topArtistRank.setText("");
            } else {
                topArtistRank.setText("#" + top_artist + " Artist");
            }

            int popInt = (int)(popularity);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setProgress(100);

            TextView popView = (TextView) findViewById(R.id.popView);
            popView.setText("Popularity: " + popInt + "%");

            artistBio = (TextView) findViewById(R.id.artistBio);


            TextView artistName = (TextView) findViewById(R.id.artistName);
            artistName.setText(artist_name);


            MainActivity.spotify.getArtistTopTrack(artist_id, "US", new retrofit.Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, retrofit.client.Response response) {
                    int i = 0;
                    while (i < 2 && tracks.tracks.get(i) != null) {
                        topTracks.add(tracks.tracks.get(i).id);
                        i++;
                    }
                    retrievedTopTracks();
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });



            OkHttpClient client = new OkHttpClient();
            String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist_name + "&api_key=9f5228be9f2d49c1700e60d8d3e02eb3&format=json";

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
                        if (json != null) {
                            JSONObject j2 = json.getJSONObject("artist");
                            if (j2 != null) {
                                JSONObject j3 = j2.getJSONObject("bio");
                                if (j3 != null) {
                                    String summary = j3.getString("summary");
                                    summary = summary.substring(0, (summary.substring(0, summary.lastIndexOf("<a"))).lastIndexOf(".") + 1);
                                    setBioText(summary.toString());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            artistBio.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.destroy();
    }
}