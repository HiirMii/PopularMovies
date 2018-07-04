package com.example.android.popularmovies.api;

import com.example.android.popularmovies.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class TMDBJsonReviewsUtils {

    /**
     * JSON response keys
     */
    private static final String MOVIE_ID = "id";
    private static final String RESULTS_KEY = "results"; // array containing movie objects
    private static final String AUTHOR_KEY = "author"; // movie review author
    private static final String CONTENT_KEY = "content"; // movie review content

    /**
     * Return a list of {@link Review} objects that has been built up from
     * parsing the given JSON response.
     */
    private static ArrayList<Review> getReviewsFromTMDBJson(String jsonReviewResponse)
            throws JSONException {

        ArrayList<Review> reviews = new ArrayList<>();

        // Create a JSONObject from the JSON response string
        JSONObject jsonResponse = new JSONObject(jsonReviewResponse);

        int movieId = jsonResponse.getInt(MOVIE_ID);

        // Extract the JSONArray associated with the key called "results",
        // which represents a list of reviews.
        JSONArray reviewsArray = jsonResponse.getJSONArray(RESULTS_KEY);

        // For each review in the results array, create an {@link Review} object
        for (int i = 0; i < reviewsArray.length(); i++) {

            // Get a single review at position i within the list of reviews
            JSONObject currentObject = reviewsArray.getJSONObject(i);

            // For a current review, extract the value for the key called "author"
            String author = currentObject.getString(AUTHOR_KEY);

            // For a current review, extract the value for the key called "content"
            String content = currentObject.getString(CONTENT_KEY);

            Review currentReview = new Review(movieId, author, content);

            reviews.add(currentReview);
        }

        return reviews;
    }

    public static ArrayList<Review> fetchReviewsData(String movieId, String keyword, String page) {

        // Create URL object
        URL reviewsRequestUrl = NetworkUtils.buildUrl(movieId, keyword, page);

        try {
            String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);

            ArrayList<Review> reviews = TMDBJsonReviewsUtils.getReviewsFromTMDBJson(jsonReviewsResponse);

            return reviews;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
