package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artists;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedArtists extends SpotifyCodeActivity {

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<Object> feed;

    private String artist_id = "";
    private String artist_name = "";

    private void fetchRelatedArtists() {
        MainActivity.spotify.getRelatedArtists(artist_id, new Callback<Artists>() {
            @Override
            public void success(Artists artists, Response response) {

                if (artists.artists.size() > 0) {
                    int numArtists = Math.min(artists.artists.size(), 10);
                    feed = new ArrayList<Object>();

                    for (int i = 0; i < numArtists; i++) {

                        String sdURL = "";
                        String hdURL = "";

                        if (artists.artists.get(i).images.size() > 0) {
                            sdURL = artists.artists.get(i).images.get(artists.artists.get(i).images.size() - 1).url;
                            hdURL = artists.artists.get(i).images.get(0).url;
                        }

                        feed.add(new ArtistItem(artists.artists.get(i).name.toString(),
                                sdURL,
                                hdURL,
                                (i % 2) == 0, artists.artists.get(i).id,
                                -1,
                                artists.artists.get(i).popularity));
                    }
                } else {
                    feed.add(new NoResultsItem());
                }

                mRecyclerViewAdapter.setTopFeed(feed);
                mRecyclerViewAdapter.notifyDataSetChanged();

                // Reset the login attempts.
                resetLoginAttempts();
            }

            @Override
            public void failure(RetrofitError error) {
                fetchNewCode(RelatedArtists.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_artists);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            artist_id = args.getString("artist_id", "");
            artist_name = args.getString("artist_name", "");

            TextView relatedArtists = (TextView) findViewById(R.id.relatedArtists);
            relatedArtists.setText("Related Artists: " + artist_name);

            fetchRelatedArtists();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mRecyclerViewAdapter = new RecyclerViewAdapter(RelatedArtists.this, recyclerView);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RelatedArtists.this));

        ImageView homeButton = (ImageView) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                /*
                    Source:
                    https://stackoverflow.com/questions/26468619/how-to-finish-all-activities-except-main-activity-and-call-another-activity
                 */
                Intent intent = new Intent(RelatedArtists.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        doActivityResult(requestCode, resultCode, intent, new myCallback() {
            @Override
            public void onSuccess() {
                fetchRelatedArtists();
            }
        });
    }
}
