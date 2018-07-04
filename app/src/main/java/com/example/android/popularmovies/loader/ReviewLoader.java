package com.example.android.popularmovies.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.api.TMDBJsonReviewsUtils;
import com.example.android.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Loads a list of reviews by using an AsyncTask to perform the
 * network request to the TMDB API.
 */
public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>> {

    private ArrayList<Review> reviewData = null;

    /**
     * Query movie ID
     */
    private String movieId;

    private AsyncTaskLoaderOnStartListener listener;

    /**
     * Constructs a new {@link ReviewLoader}.
     *
     * @param context        of the activity
     * @param currentMovieId used to build the URL
     */
    public ReviewLoader(Context context, String currentMovieId, AsyncTaskLoaderOnStartListener reviewListener) {
        super(context);
        movieId = currentMovieId;
        listener = reviewListener;
    }

    @Override
    protected void onStartLoading() {

        if (reviewData != null) {
            deliverResult(reviewData);
        } else {
            listener.showProgressBar();
            forceLoad();
        }
    }

    @Override
    public ArrayList<Review> loadInBackground() {

        return TMDBJsonReviewsUtils.fetchReviewsData(movieId, "reviews", null);
    }

    @Override
    public void deliverResult(ArrayList<Review> data) {
        reviewData = data;
        super.deliverResult(data);
    }
}
