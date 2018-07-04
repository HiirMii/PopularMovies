package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An {@link MovieAdapter} knows how to create a list item layout for each movie
 * in the data source (a list of {@link Movie} objects).
 * <p>
 * These list item layouts will be provided to an adapter view (RecyclerView)
 * to be displayed to the user.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {


    private final MovieAdapterOnClickHandler movieAdapterOnClickHandler;
    private ArrayList<Movie> moviesList = new ArrayList<>();
    private Context context;

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        movieAdapterOnClickHandler = clickHandler;
    }

    /**
     * Create new views (invoked by the layout manager).
     */
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {

        final Movie currentMovie = moviesList.get(position);

        Picasso.with(context)
                .load(currentMovie.getPosterPath())
                .into(holder.moviePoster);
    }

    /**
     * Returns the size of the list.
     */
    @Override
    public int getItemCount() {
        if (moviesList == null) return 0;
        return moviesList.size();
    }

    /**
     * Used to populate the RecyclerView with the movies list from internet
     */
    public void setMoviesList(ArrayList<Movie> moviesList) {
        this.moviesList.clear();
        this.moviesList.addAll(moviesList);
        notifyDataSetChanged();
    }

    /**
     * Used to populate the RecyclerView with the movies list from database
     */
    public void setMoviesListFromCursor(Cursor cursor) {
        this.moviesList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int movieId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
                String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
                String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_PATH));
                String backdropPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                Double voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE));
                String overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW));

                Movie movie = new Movie(movieId, title, posterPath, backdropPath, releaseDate, voteAverage, overview);
                this.moviesList.add(movie);

            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView moviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        /**
         * Triggered when the list item is clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = moviesList.get(adapterPosition);
            movieAdapterOnClickHandler.onClick(currentMovie);
        }
    }
}
