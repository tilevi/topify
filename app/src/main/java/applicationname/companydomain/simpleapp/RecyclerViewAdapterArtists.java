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

public class RecyclerViewAdapterArtists extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private List<ArtistItem> topFeed = new ArrayList();
    private Context context;
    private RecyclerView mRecyclerView;

    // Constructor
    public RecyclerViewAdapterArtists(Context context, RecyclerView mRecyclerView){
        this.context = context;
        this.mRecyclerView = mRecyclerView;
    }

    public void setTopFeed(List<ArtistItem> topFeed) {
        this.topFeed = topFeed;
    }

    // Invoked by layout manager to replace the contents of the views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ArtistItem artistItem = (ArtistItem) topFeed.get(position);
        ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
        artistViewHolder.setDetails(artistItem);
    }

    @Override
    public int getItemCount(){
        return topFeed.size();
    }

    // Invoked by layout manager to create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.layout_artist;
        RecyclerView.ViewHolder viewHolder;

        View artistView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        viewHolder = new ArtistViewHolder(artistView, mListener);

        return viewHolder;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private ImageView artistImage;
        private ConstraintLayout parentLayout;

        public ArtistViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.itemName);
            artistImage = (ImageView)itemView.findViewById(R.id.trackImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ArtistDetailsActivity.class);

                        ArtistItem artistItem = (ArtistItem) topFeed.get(position);
                        intent.putExtra("artist_id", artistItem.getID());
                        intent.putExtra("artist_name", artistItem.getName());
                        intent.putExtra("artist_url", artistItem.getHdURL());
                        intent.putExtra("top_artist", -1);
                        intent.putExtra("popularity", artistItem.getPopularity());

                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setDetails(ArtistItem artistItem) {
            if (artistItem.getTheColor()) {
                parentLayout.setBackgroundColor(Color.parseColor("#485771"));
            } else {
                parentLayout.setBackgroundColor(Color.parseColor("#3F495B"));
            }

            itemName.setText(artistItem.getName());

            // Get the URL
            String url = artistItem.getURL();

            if (!(url.equals(""))) {
                Glide.with(context)
                        .load(artistItem.getURL())
                        .apply(RequestOptions.circleCropTransform())
                        .into(artistImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown)
                        .apply(RequestOptions.circleCropTransform())
                        .into(artistImage);
            }
        }
    }
}