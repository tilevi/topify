package applicationname.companydomain.simpleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.util.Log;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;

import android.widget.ImageView;
import android.widget.TextView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
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
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

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
    protected void onResume() {
        super.onResume();
    }

    private void fetchNewToken() {
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(LoginActivity.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, LoginActivity.REDIRECT_URI);
        builder.setScopes(new String[]{"user-top-read", "user-read-private"});
        //builder.setShowDialog(true);
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(MainActivity.this,
                LoginActivity.REQUEST_CODE, request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String token = args.getString("ACCESS_TOKEN", "");
            Log.d("onCreate token", token);
            spotifyApi.setAccessToken(token);

            // Fetch our Spotify profile
            fetchMyAvatar();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mSwipeRefreshLayout.setDistanceToTriggerSync(600);// in dips
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
                            artists.items.get(i).images.get(0).url,
                            (i % 2) == 0, artists.items.get(i).id,
                            (i+1),
                            artists.items.get(i).popularity));
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
                fetchNewToken();
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

                getTrackFeatures(tracks.items);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchNewToken();
                Log.d("TopTracksFAILURE", error.toString());
            }
        });
    }

    public void onLogoutButtonClicked(View v) {
        onMediumTermClicked(v);
        /*LoginActivity.tokenManager.clearToken();
        spotifyApi.setAccessToken("");

        AuthenticationClient.logout(MainActivity.this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("login", true);
        startActivity(intent);
        finish();*/
    }

    public void onRefreshTokenClicked(View v) {
        spotifyApi.setAccessToken("");
        /*
        LoginActivity.tokenManager.getNewToken(new myCallback() {
            @Override
            public void onSuccess(String a_token, String r_token) {
                LoginActivity.tokenManager.setTokens(a_token, null);
            }

            @Override
            public void onError(String err) {
                Log.d("onError", "Failed to retrieve new access token.");
            }
        });*/
    }

    private void fetchMyAvatar() {
        // Load my username or (display name)
        spotify.getMe(new Callback<UserPrivate>() {

            public void success(UserPrivate userPrivate, Response response) {

                String displayName = userPrivate.display_name;
                if (displayName == null || displayName.trim().equals("")) {
                    displayName = userPrivate.id;
                }

                Object url; // We don't know if it's going to be a string or integer.
                if (userPrivate.images.size() > 0) {
                    url = userPrivate.images.get(userPrivate.images.size() - 1).url;
                } else {
                    url = R.drawable.unknown;
                }

                ImageView avatar = (ImageView) findViewById(R.id.avatarView);
                ImageView avatar2 = (ImageView) findViewById(R.id.avatarView2);

                Glide.with(MainActivity.this)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatar);

                avatar2.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                });

                // Fetch our top artists and tracks
                fetchTopArtists();
            }

            public void failure(RetrofitError error) {
                fetchNewToken();
                Log.d("getMe FAILURE", error.toString());
            }
        });
    }

    // We want to set it to synchronized just in-case.
    private synchronized void refreshItems() {
        // Fetch our profile
        fetchMyAvatar();
    }

    private void getTrackFeatures(final List<Track> items) {

        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < items.size(); i++) {
            if (i != (items.size() - 1)) {
                sb.append(items.get(i).id + ",");
            } else {
                sb.append(items.get(i).id);
            }
        }

        spotify.getTracksAudioFeatures(sb.toString(), new Callback<AudioFeaturesTracks>() {
            @Override
            public void success(AudioFeaturesTracks features, Response response) {

                List<AudioFeaturesTrack> audioFeaturesTracks = features.audio_features;

                for (int i = 0; i < audioFeaturesTracks.size(); i++) {
                    if (audioFeaturesTracks.get(i) != null) {
                        feed.add(new TrackItem(items.get(i).name.toString(),
                                items.get(i).artists.get(0).name.toString(),
                                items.get(i).album.images.get(items.get(i).album.images.size() - 1).url,
                                (i % 2) == 0, items.get(i).id,
                                (i+1),
                                items.get(i).popularity,
                                audioFeaturesTracks.get(i).danceability,
                                audioFeaturesTracks.get(i).energy,
                                audioFeaturesTracks.get(i).valence));
                    } else {
                        feed.add(new TrackItem(items.get(i).name.toString(),
                                items.get(i).artists.get(0).name.toString(),
                                items.get(i).album.images.get(items.get(i).album.images.size() - 1).url,
                                (i % 2) == 0, items.get(i).id,
                                (i+1),
                                items.get(i).popularity,
                                -1f,
                                -1f,
                                -1f));
                    }
                }
                onItemsLoadComplete();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    void onItemsLoadComplete() {
        // Set the new top feed and update the RecyclerView
        mRecyclerViewAdapter.setTopFeed(feed);
        mRecyclerViewAdapter.notifyDataSetChanged();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LoginActivity.REQUEST_CODE) {

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                String access_token = response.getAccessToken();
                LoginActivity.tokenManager.setTokens(access_token, "");
                spotifyApi.setAccessToken(access_token);
                refreshItems();
            }
        }
    }
}
