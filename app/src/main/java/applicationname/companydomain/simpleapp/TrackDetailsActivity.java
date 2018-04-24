package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TrackDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String track_id = args.getString("track_id", "");
            String track_title = args.getString("track_title", "");
            String track_artist = args.getString("track_artist", "");

            TextView textView = findViewById(R.id.textView);
            TextView textView2 = findViewById(R.id.textView2);

            textView.setText(track_title);
            textView2.setText(track_artist);
        }
    }
}