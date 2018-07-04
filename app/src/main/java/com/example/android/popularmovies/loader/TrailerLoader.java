package com.example.android.popularmovies.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.api.TMDBJsonTrailersUtils;
import com.example.android.popularmovies.model.Trailer;

import java.util.ArrayList;

/**
 * Loads a list of trailers by using an AsyncTask to perform the
 * network request to the TMDB API.
 */
public class TrailerLoader extends AsyncTaskLoader<ArrayList<Trailer>> {

    private ArrayList<Trailer> trailerData = null;

    /**
     * Query movie ID
     */
    private String movieId;

    private AsyncTaskLoaderOnStartListener listener;

    /**
     * Constructs a new {@link TrailerLoader}.
     *
     * @param context        of the activity
     * @param currentMovieId used to build the URL
     */
    public TrailerLoader(Context context, String currentMovieId, AsyncTaskLoaderOnStartListener trailerListener) {
        super(context);
        movieId = currentMovieId;
        listener = trailerListener;
    }

    @Override
    protected void onStartLoading() {

        if (trailerData != null) {
            deliverResult(trailerData);
        } else {
            listener.showProgressBar();
            forceLoad();
        }
    }

    @Override
    public ArrayList<Trailer> loadInBackground() {

        return TMDBJsonTrailersUtils.fetchTrailersData(movieId, "videos", null);
    }

    @Override
    public void deliverResult(ArrayList<Trailer> data) {
        trailerData = data;
        super.deliverResult(data);
    }
}
