package applicationname.companydomain.simpleapp;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArtistDetailsActivity extends AppCompatActivity {

    private TextView artistBio = null;

    private void setBioText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                artistBio.setText(text);
                Log.d("ArtistBio", "set text");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String artist_id = args.getString("artist_id", "");
            String artist_name = args.getString("artist_name", "");
            String artist_url = args.getString("artist_url", "");
            Integer top_artist = args.getInt("top_artist", -1);

            float popularity = args.getFloat("popularity", -1);

            TextView textView = (TextView) findViewById(R.id.topArtistRank);
            textView.setText("#" + top_artist + " Artist");

            ImageView artistImage = (ImageView) findViewById(R.id.imageView);

            Glide.with(ArtistDetailsActivity.this)
                    .load(artist_url)
                    .into(artistImage);

            if (top_artist == -1) {
                ConstraintLayout topArtistLayout = (ConstraintLayout) findViewById(R.id.topTrackLayout);
                topArtistLayout.setVisibility(View.GONE);
            }

            int popInt = (int)(popularity);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(popInt);

            TextView popView = (TextView) findViewById(R.id.popView);
            popView.setText("Popularity: " + popInt + "%");

            artistBio = (TextView) findViewById(R.id.artistBio);


            TextView artistName = (TextView) findViewById(R.id.artistName);
            artistName.setText(artist_name);

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
}