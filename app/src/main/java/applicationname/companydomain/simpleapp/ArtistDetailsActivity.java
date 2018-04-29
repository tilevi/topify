package applicationname.companydomain.simpleapp;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ArtistDetailsActivity extends AppCompatActivity {

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
        }
    }
}