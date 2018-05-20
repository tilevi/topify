package applicationname.companydomain.simpleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artists;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedArtists extends SpotifyCodeActivity {

    private RecyclerViewAdapterArtists mRecyclerViewAdapter;
    private ArrayList<ArtistItem> feed;

    private String artist_id = "";
    private String artist_name = "";

    private void fetchRelatedArtists() {
        MainActivity.spotify.getRelatedArtists(artist_id, new Callback<Artists>() {
            @Override
            public void success(Artists artists, Response response) {

                int numArtists = Math.min(artists.artists.size(), 10);
                feed = new ArrayList<ArtistItem>();

                for (int i = 0; i < numArtists; i++) {
                    feed.add(new ArtistItem(artists.artists.get(i).name.toString(),
                            artists.artists.get(i).images.get(artists.artists.get(i).images.size() - 1).url,
                            artists.artists.get(i).images.get(0).url,
                            (i % 2) == 0, artists.artists.get(i).id,
                            (i+1),
                            artists.artists.get(i).popularity));
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                mRecyclerViewAdapter = new RecyclerViewAdapterArtists(RelatedArtists.this, recyclerView);
                recyclerView.setAdapter(mRecyclerViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(RelatedArtists.this));

                mRecyclerViewAdapter.setTopFeed(feed);
                mRecyclerViewAdapter.notifyDataSetChanged();
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
