package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.TrailerContract.TrailerEntry;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An {@link TrailerAdapter} knows how to create a list item layout for each trailer
 * in the data source (a list of {@link Trailer} objects).
 * <p>
 * These list item layouts will be provided to an adapter view (RecyclerView)
 * to be displayed to the user.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final TrailerAdapterOnClickHandler trailerAdapterOnClickHandler;
    private ArrayList<Trailer> trailersList;
    private Context context;

    /**
     * Creates a TrailerAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(ArrayList<Trailer> trailers, TrailerAdapterOnClickHandler clickHandler) {
        this.trailersList = trailers;
        trailerAdapterOnClickHandler = clickHandler;
    }

    /**
     * Create new views (invoked by the layout manager).
     */
    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        final Trailer currentTrailer = trailersList.get(position);
        String trailerThumbnailUrl = currentTrailer.getMovieTrailerThumbnail();

        Picasso.with(context)
                .load(trailerThumbnailUrl)
                .into(holder.movieTrailer);

        holder.movieTrailerName.setText(currentTrailer.getMovieTrailerTitle());
    }

    /**
     * Returns the size of the list.
     */
    @Override
    public int getItemCount() {
        if (trailersList == null) return 0;
        return trailersList.size();
    }

    /**
     * Used to populate the RecyclerView with the movie trailers list from the internet
     */
    public void setTrailersList(ArrayList<Trailer> trailersList) {
        this.trailersList = new ArrayList<>();
        this.trailersList.clear();
        this.trailersList.addAll(trailersList);
        notifyDataSetChanged();
    }

    /**
     * Used to populate the RecyclerView with the movie trailers list from the database
     */
    public void setTrailersListFromCursor(Cursor cursor) {
        this.trailersList = new ArrayList<>();
        this.trailersList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int movieId = cursor.getInt(cursor.getColumnIndex(TrailerEntry.COLUMN_MOVIE_ID));
                String movieTrailerTitle = cursor.getString(
                        cursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_TITLE));
                String movieTrailerThumbnail = cursor.getString(
                        cursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_THUMBNAIL));
                String movieTrailerLink = cursor.getString(
                        cursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_LINK));

                Trailer trailer = new Trailer(movieId, movieTrailerTitle, movieTrailerThumbnail, movieTrailerLink);
                this.trailersList.add(trailer);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView movieTrailer;
        public final TextView movieTrailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            movieTrailer = itemView.findViewById(R.id.iv_trailer);
            movieTrailer.setOnClickListener(this);

            movieTrailerName = itemView.findViewById(R.id.tv_trailer_name);
        }

        /**
         * Triggered when the list item is clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer currentTrailer = trailersList.get(adapterPosition);
            trailerAdapterOnClickHandler.onClick(currentTrailer);
        }
    }
}
