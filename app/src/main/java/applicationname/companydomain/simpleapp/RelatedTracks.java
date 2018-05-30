package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedTracks extends SpotifyCodeActivity {

    private RecyclerViewAdapterTracks mRecyclerViewAdapter;
    private ArrayList<TrackItem> feed;

    private String track_id = "";
    private String track_name = "";

    private void getTrackFeatures(final List<Track> items) {

        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < items.size(); i++) {
            if (i != (items.size() - 1)) {
                sb.append(items.get(i).id + ",");
            } else {
                sb.append(items.get(i).id);
            }
        }

        MainActivity.spotify.getTracksAudioFeatures(sb.toString(), new Callback<AudioFeaturesTracks>() {
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
                fetchNewCode(RelatedTracks.this);
            }
        });
    }

    private void fetchRelatedTracks() {
        // Fetch the related tracks via the Spotify API
        Map<String, Object> options = new HashMap<>();
        options.put("limit", 10);
        options.put("seed_tracks", track_id);

        MainActivity.spotify.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                List<Track> tracks = recommendations.tracks;

                getTrackFeatures(tracks);


                //for (Track track : tracks) {

                //}

                //feed.add(new CategoryItem("Top Tracks", time_range));
                //getTrackFeatures(tracks.items);
            }

            @Override
            public void failure(RetrofitError error) {
                // fetchNewCode(MainActivity.this);
            }
        });
    }

    private void onItemsLoadComplete() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mRecyclerViewAdapter = new RecyclerViewAdapterTracks(RelatedTracks.this, recyclerView);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RelatedTracks.this));

        mRecyclerViewAdapter.setTopFeed(feed);
        mRecyclerViewAdapter.notifyDataSetChanged();

        // Reset the login attempts.
        resetLoginAttempts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_tracks);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            track_id = args.getString("track_id", "");
            track_name = args.getString("track_name", "");

            //TextView relatedTracks = (TextView) findViewById(R.id.rela);
            //relatedArtists.setText("Related Artists: " + artist_name);

            fetchRelatedTracks();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        doActivityResult(requestCode, resultCode, intent, new myCallback() {
            @Override
            public void onSuccess() {
                fetchRelatedTracks();
            }
        });
    }
}
