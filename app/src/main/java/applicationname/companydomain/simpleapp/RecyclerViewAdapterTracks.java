package applicationname.companydomain.simpleapp;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.graphics.Color;

import kaaes.spotify.webapi.android.models.Track;

public class RecyclerViewAdapterTracks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private List<TrackItem> topFeed = new ArrayList();
    private Context context;
    private RecyclerView mRecyclerView;

    // Constructor
    public RecyclerViewAdapterTracks(Context context, RecyclerView mRecyclerView){
        this.context = context;
        this.mRecyclerView = mRecyclerView;
    }

    public void setTopFeed(List<TrackItem> topFeed) {
        this.topFeed = topFeed;
    }

    // Invoked by layout manager to replace the contents of the views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        TrackItem trackItem = (TrackItem) topFeed.get(position);
        TrackViewHolder trackViewHolder = (TrackViewHolder) holder;
        trackViewHolder.setDetails(trackItem);
    }

    @Override
    public int getItemCount(){
        return topFeed.size();
    }

    // Invoked by layout manager to create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.layout_track;
        RecyclerView.ViewHolder viewHolder;

        View trackView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        viewHolder = new TrackViewHolder(trackView);

        return viewHolder;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView artistName;
        private ImageView trackImage;
        private ConstraintLayout parentLayout;


        public TrackViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView)itemView.findViewById(R.id.itemName);
            artistName = (TextView)itemView.findViewById(R.id.artistName);
            trackImage = (ImageView) itemView.findViewById(R.id.trackImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, TrackDetailsActivity.class);

                        TrackItem trackItem = (TrackItem) topFeed.get(position);
                        intent.putExtra("track_id", trackItem.getID());
                        intent.putExtra("track_title", trackItem.getTitle());
                        intent.putExtra("track_artist", trackItem.getArtist());
                        intent.putExtra("top_track", -1);

                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setDetails(TrackItem trackItem) {
            if (trackItem.getTheColor()) {
                parentLayout.setBackgroundColor(Color.parseColor("#485771"));
            } else {
                parentLayout.setBackgroundColor(Color.parseColor("#3F495B"));
            }

            itemName.setText(trackItem.getTitle());
            artistName.setText(trackItem.getArtist());

            Glide.with(context)
                    .load(trackItem.getURL())
                    .into(trackImage);
        }
    }

}