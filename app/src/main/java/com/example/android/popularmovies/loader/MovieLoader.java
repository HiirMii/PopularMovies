package com.example.android.popularmovies.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.api.TMDBJsonMoviesUtils;
import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;

/**
 * Loads a list of movies by using an AsyncTask to perform the
 * network request to the TMDB API.
 */
public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private ArrayList<Movie> movieData = null;

    /**
     * Query keyword
     */
    private String keyword;

    /**
     * Query page
     */
    private String page;

    private AsyncTaskLoaderOnStartListener listener;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context        of the activity
     * @param currentKeyword used to build the URL
     */
    public MovieLoader(Context context, String currentKeyword, String currentPage, AsyncTaskLoaderOnStartListener movieListener) {
        super(context);
        keyword = currentKeyword;
        page = currentPage;
        listener = movieListener;
    }

    @Override
    protected void onStartLoading() {

        if (movieData != null) {
            deliverResult(movieData);
        } else {
            listener.showProgressBar();
            forceLoad();
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {

        if (keyword.equals("favourites")) {
            return null;
        } else {
            return TMDBJsonMoviesUtils.fetchMoviesData(null, keyword, page);
        }
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        movieData = data;
        super.deliverResult(data);
    }

}