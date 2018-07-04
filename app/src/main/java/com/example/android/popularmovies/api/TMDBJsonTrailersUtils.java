package com.example.android.popularmovies.api;

import com.example.android.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class TMDBJsonTrailersUtils {

    /**
     * JSON response keys
     */
    private static final String MOVIE_ID = "id";
    private static final String RESULTS_KEY = "results";
    private static final String TRAILER_KEY = "key";
    private static final String NAME_KEY = "name";

    /**
     * Return a list of {@link Trailer} objects that has been built up from
     * parsing the given JSON response.
     */
    private static ArrayList<Trailer> getTrailersFromTMDBJson(String jsonTrailerResponse)
            throws JSONException {

        ArrayList<Trailer> trailers = new ArrayList<>();

        // Create a JSONObject from the JSON response string
        JSONObject jsonResponse = new JSONObject(jsonTrailerResponse);

        int movieId = jsonResponse.getInt(MOVIE_ID);

        // Extract the JSONArray associated with the key called "results",
        // which represents a list of trailers.
        JSONArray trailersArray = jsonResponse.getJSONArray(RESULTS_KEY);

        // For each trailer in the results array, create an {@link Trailer} object
        for (int i = 0; i < trailersArray.length(); i++) {

            // Get a single trailer at position i within the list of trailers
            JSONObject currentObject = trailersArray.getJSONObject(i);

            // For a current trailer, extract the value for the key called "key"
            String key = currentObject.getString(TRAILER_KEY);

            String thumbnail = "http://img.youtube.com/vi/" + key + "/0.jpg";

            String link = "http://www.youtube.com/watch?v=" + key;

            // For a current trailer, extract the value for the key called "name"
            String title = currentObject.getString(NAME_KEY);

            Trailer currentTrailer = new Trailer(movieId, title, thumbnail, link);

            trailers.add(currentTrailer);
        }

        return trailers;
    }

    public static ArrayList<Trailer> fetchTrailersData(String movieId, String keyword, String page) {

        // Create URL object
        URL trailersRequestUrl = NetworkUtils.buildUrl(movieId, keyword, page);

        try {
            String jsonTrailersResponse = NetworkUtils.getResponseFromHttpUrl(trailersRequestUrl);

            ArrayList<Trailer> trailers = TMDBJsonTrailersUtils.getTrailersFromTMDBJson(jsonTrailersResponse);

            return trailers;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
