package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<Object> feed;

    private String track_id = "";
    private String track_title = "";
    private String track_artist = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_tracks);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            track_id = args.getString("track_id", "");
            track_title = args.getString("track_name", "");
            track_artist = args.getString("track_artist", "");

            TextView relatedTracks = (TextView) findViewById(R.id.relatedTracks);
            relatedTracks.setText("Related Tracks: " + track_title + " - " + track_artist);

            fetchRelatedTracks();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mRecyclerViewAdapter = new RecyclerViewAdapter(RelatedTracks.this, recyclerView);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RelatedTracks.this));

        ImageView homeButton = (ImageView) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                /*
                    Source:
                    https://stackoverflow.com/questions/26468619/how-to-finish-all-activities-except-main-activity-and-call-another-activity
                 */
                Intent intent = new Intent(RelatedTracks.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private void onItemsLoadComplete() {
        mRecyclerViewAdapter.setTopFeed(feed);
        mRecyclerViewAdapter.notifyDataSetChanged();

        // Reset the login attempts.
        resetLoginAttempts();
    }

    private void getTrackFeatures(final List<Track> items) {
        if (items.size() > 0) {

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

                    feed = new ArrayList<Object>();
                    List<AudioFeaturesTrack> audioFeaturesTracks = features.audio_features;

                    for (int i = 0; i < items.size(); i++) {

                        String url = "";
                        String artist = "";

                        if (items.get(i).album != null && items.get(i).album.images != null
                                && items.get(i).album.images.size() > 0) {
                            url = items.get(i).album.images.get(items.get(i).album.images.size() - 1).url;
                            artist = items.get(i).artists.get(0).name.toString();
                        }

                        if (audioFeaturesTracks.get(i) != null) {
                            feed.add(new TrackItem(items.get(i).name.toString(),
                                    artist,
                                    url,
                                    (i % 2) == 0, items.get(i).id,
                                    -1,
                                    items.get(i).popularity,
                                    audioFeaturesTracks.get(i).danceability,
                                    audioFeaturesTracks.get(i).energy,
                                    audioFeaturesTracks.get(i).valence));
                        } else {
                            feed.add(new TrackItem(items.get(i).name.toString(),
                                    artist,
                                    url,
                                    (i % 2) == 0, items.get(i).id,
                                    -1,
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
        } else {
            feed.add(new NoResultsItem());
        }
    }

    private void fetchRelatedTracks() {
        // Fetch the related tracks via the Spotify API
        Map<String, Object> options = new HashMap<>();
        options.put("limit", 10);
        options.put("seed_tracks", track_id);

        MainActivity.spotify.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                getTrackFeatures(recommendations.tracks);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchNewCode(RelatedTracks.this);
            }
        });
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