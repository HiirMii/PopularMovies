package com.example.android.popularmovies.api;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with The Movie Database (TMDB) servers.
 */
public class NetworkUtils {

    /**
     * Base Url for TMDB media query
     */
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    /**
     * Query parameter used to append TMDB API Key to the URL
     */
    private static final String API_KEY = "api_key";

    /**
     * Query parameter used to append query page to the URL
     */
    private static final String PAGE = "page";

    /**
     * This is where you should place your TMDB API KEY used to access TMDB database
     */
    private static final String TMDB_API_KEY = "your key goes here";

    /**
     * Tag for the log messages
     */
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Builds the URL used to  query the TMDB server.
     *
     * @param movieId specified for fetching reviews and trailers for current movie.
     * @param keyword specified to display data in desired order (popular or top_rated),
     *                or in conjunction with movieId to fetch reviews or trailers
     * @return The URL to use to query the TMDB server
     */
    public static URL buildUrl(String movieId, String keyword, String page) {
        Uri.Builder buildUri = Uri.parse(BASE_URL).buildUpon();

        if (movieId != null) {
            buildUri.appendPath(movieId);
        }

        if (keyword != null) {
            buildUri.appendPath(keyword);
        }

        buildUri.appendQueryParameter(API_KEY, TMDB_API_KEY);

        if (page != null) {
            buildUri.appendQueryParameter(PAGE, page);
        }

        buildUri.build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
