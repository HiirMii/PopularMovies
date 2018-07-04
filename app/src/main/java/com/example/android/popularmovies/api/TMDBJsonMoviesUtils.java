package com.example.android.popularmovies.api;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


/**
 * Utility functions to handle TMDB JSON data.
 */
public class TMDBJsonMoviesUtils {

    /**
     * JSON response keys
     */
    private static final String RESULTS_KEY = "results"; // array containing movie objects
    private static final String ID_KEY = "id"; // movie object id
    private static final String TITLE_KEY = "title"; // movie object title
    private static final String POSTER_PATH_KEY = "poster_path"; // movie object poster relative url
    private static final String BACKDROP_PATH_KEY = "backdrop_path"; // movie object backdrop image relative url
    private static final String RELEASE_DATE_KEY = "release_date"; // movie object release date
    private static final String VOTE_AVERAGE_KEY = "vote_average"; // movie object vote average
    private static final String OVERVIEW_KEY = "overview"; // movie object short overview

    /**
     * Base URL for the poster
     */
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    /**
     * Base URL for the backdrop image
     */
    private static final String BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w342/";

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    public static ArrayList<Movie> getMoviesFromTMDBJson(String jsonMovieResponse)
            throws JSONException {

        ArrayList<Movie> movies = new ArrayList<>();

        // Create a JSONObject from the JSON response string
        JSONObject jsonResponse = new JSONObject(jsonMovieResponse);

        // Extract the JSONArray associated with the key called "results",
        // which represents a list of movies.
        JSONArray moviesArray = jsonResponse.getJSONArray(RESULTS_KEY);

        // For each movie in the results array, create an {@link Movie} object
        for (int i = 0; i < moviesArray.length(); i++) {

            // Get a single movie at position i within the list of movies
            JSONObject currentObject = moviesArray.getJSONObject(i);

            // For a current movie, extract the value for the key called "id"
            int id = currentObject.getInt(ID_KEY);

            // For a current movie, extract the value for the key called "title"
            String title = currentObject.getString(TITLE_KEY);

            // For a current movie, extract the value for the key called "poster_path"
            String posterPath = BASE_POSTER_URL + currentObject.getString(POSTER_PATH_KEY);

            // For a current movie, extract the value for the key called "backdrop_path"
            String backdropPath = BASE_BACKDROP_URL + currentObject.getString(BACKDROP_PATH_KEY);

            // For a current movie, extract the value for the key called "release_date"
            String releaseDate = currentObject.getString(RELEASE_DATE_KEY);

            // For a current movie, extract the value for the key called "vote_average"
            Double voteAverage = currentObject.getDouble(VOTE_AVERAGE_KEY);

            // For a current movie, extract the value for the key called "overview"
            String overview = currentObject.getString(OVERVIEW_KEY);

            // Create a new {@link Movie} object with the title, posterPath, voteAverage, overview
            // and releaseDate from the JSON response.
            Movie currentMovie = new Movie(id, title, posterPath, backdropPath, releaseDate, voteAverage, overview);

            // Add the new {@link Movie} to the list of movies.
            movies.add(currentMovie);
        }

        return movies;
    }

    public static ArrayList<Movie> fetchMoviesData(String movieId, String keyword, String page) {

        // Create URL object
        URL moviesRequestUrl = NetworkUtils.buildUrl(movieId, keyword, page);

        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

            ArrayList<Movie> movies = TMDBJsonMoviesUtils.getMoviesFromTMDBJson(jsonMoviesResponse);

            return movies;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
