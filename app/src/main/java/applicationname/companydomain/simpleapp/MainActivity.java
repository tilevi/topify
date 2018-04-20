package applicationname.companydomain.simpleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.util.Log;


import android.widget.TextView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.Pager;

import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;
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
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;


import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    static final SpotifyApi spotifyApi = new SpotifyApi();
    static final SpotifyService spotify = spotifyApi.getService();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private ArrayList<Object> feed;
    private String time_range = "short_term";

    public static final Map<String, String> TIME_LABELS = createTimeLabels();
    private static Map<String, String> createTimeLabels() {
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("short_term", "Based on the past 4 weeks.");
        labels.put("medium_term", "Based on the past 6 months.");
        labels.put("long_term", "Based on several years.");

        return labels;
    }

    private static final Map<String, String> TERM_LABELS = createTermLabels();
    private static Map<String, String> createTermLabels() {
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("short_term", "Short-term");
        labels.put("medium_term", "Medium-term");
        labels.put("long_term", "Long-term");

        return labels;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String token = args.getString("ACCESS_TOKEN", "");
            Log.d("onCreate token", token);
            spotifyApi.setAccessToken(token);

            // Load my username or (display name)
            /*spotify.getMe(new Callback<UserPrivate>() {

                public void success(UserPrivate userPrivate, Response response) {

                    String displayName = userPrivate.display_name;
                    if (displayName == null || displayName.trim().equals("")) {
                        displayName = userPrivate.id;
                    }
                    Log.d("TheUsername", displayName);

                    TextView textView = (TextView) findViewById(R.id.username);
                    textView.setText(displayName);
                }

                public void failure(RetrofitError error) {
                    Log.d("getMe FAILURE", error.toString());
                }
            });*/

            fetchTopArtists();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mSwipeRefreshLayout.setDistanceToTriggerSync(800);// in dips
    }

    private void setHeaderLabel() {
        TextView timeLabel = (TextView) findViewById(R.id.timeLabel);
        timeLabel.setText(MainActivity.TERM_LABELS.get(time_range));
    }

    public void onShortTermClicked(View v) {
        time_range = "short_term";
        setHeaderLabel();
        refreshItems();
    }

    public void onMediumTermClicked(View v) {
        time_range = "medium_term";
        setHeaderLabel();
        refreshItems();
    }

    public void onLongTermClicked(View v) {
        time_range = "long_term";
        setHeaderLabel();
        refreshItems();
    }

    private void fetchTopArtists() {
        Map<String, Object> options = new HashMap<>();
        options.put("time_range", time_range);

        spotify.getTopArtists(options, new Callback<Pager<Artist>>() {
            @Override
            public void success(Pager<Artist> artists, Response response) {

                feed = new ArrayList<>();

                feed.add(new CategoryItem("Top Artists", time_range));

                for (int i = 0; i < artists.items.size(); i++) {
                    feed.add(new ArtistItem(artists.items.get(i).name.toString(),
                            artists.items.get(i).images.get(artists.items.get(i).images.size() - 1).url,
                            (i % 2) == 0));
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                mRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, recyclerView);
                recyclerView.setAdapter(mRecyclerViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ArtistItem artistItem = (ArtistItem) feed.get(position);
                        Log.d("OnArtistClicked", artistItem.getName());
                    }
                });

                fetchTopTracks();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TopArtistsFAILURE", error.toString());
            }
        });
    }

    private void fetchTopTracks() {
        Map<String, Object> options = new HashMap<>();
        options.put("time_range", time_range);

        spotify.getTopTracks(options, new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> tracks, Response response) {

                feed.add(new CategoryItem("Top Tracks", time_range));

                for (int i = 0; i < tracks.items.size(); i++) {
                    feed.add(new TrackItem(tracks.items.get(i).name.toString(),
                            tracks.items.get(i).artists.get(0).name.toString(),
                            tracks.items.get(i).album.images.get(tracks.items.get(i).album.images.size() - 1).url,
                            (i % 2) == 0));
                }

                onItemsLoadComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TopTracksFAILURE", error.toString());
            }
        });
    }

    public void onLogoutButtonClicked(View v) {
        LoginActivity.tokenManager.clearToken();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("login", true);
        startActivity(intent);
        finish();
    }

    public void onRefreshTokenClicked(View v) {
        LoginActivity.tokenManager.getNewToken(new myCallback() {
            @Override
            public void onSuccess(String a_token, String r_token) {
                LoginActivity.tokenManager.setTokens(a_token, null);
            }

            @Override
            public void onError(String err) {
                Log.d("onError", "Failed to retrieve new access token.");
            }
        });
    }

    void refreshItems() {
        // Fetch our top artists
        fetchTopArtists();
    }

    void onItemsLoadComplete() {
        // Set the new top feed and update the RecyclerView
        mRecyclerViewAdapter.setTopFeed(feed);
        mRecyclerViewAdapter.notifyDataSetChanged();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
