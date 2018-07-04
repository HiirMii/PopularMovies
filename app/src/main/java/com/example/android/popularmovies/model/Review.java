package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Review} object contains information related to a single movie review.
 */

public class Review implements Parcelable {

    /**
     * Converts Parcel back to the Review object
     */
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    /**
     * Id of the movie
     */
    int movieId;

    /**
     * Author of the movie review
     */
    private String reviewAuthor;

    /**
     * Content of the movie review
     */
    private String reviewContent;

    /**
     * Constructs a new {@link Review} object.
     *
     * @param movieId       is the movie ID
     * @param reviewAuthor  is the author of the movie review
     * @param reviewContent is the content of the movie review
     */
    public Review(int movieId, String reviewAuthor, String reviewContent) {
        this.movieId = movieId;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    /**
     * Retrieving Review object fields from the Parcel
     */
    private Review(Parcel in) {
        movieId = in.readInt();
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    /**
     * Getters for the {@link Review} object data.
     */

    public int getMovieId() {
        return movieId;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens Review object into a Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
    }
}
