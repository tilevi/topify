package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;

public class ArtistDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String artist_id = args.getString("artist_id", "");
            String artist_name = args.getString("artist_name", "");

            TextView textView = findViewById(R.id.textView);
            textView.setText(artist_name);
        }
    }
}