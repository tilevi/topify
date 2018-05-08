package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedArtists extends AppCompatActivity {

    private RecyclerViewAdapterArtists mRecyclerViewAdapter;
    private ArrayList<ArtistItem> feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_artists);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String artist_id = args.getString("artist_id", "");
            String artist_name = args.getString("artist_name", "");

            TextView relatedArtists = (TextView) findViewById(R.id.relatedArtists);
            relatedArtists.setText("Related Artists: " + artist_name);

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
                    Log.d("TopArtistsFAILURE", error.toString());
                }
            });

        }
    }
}
