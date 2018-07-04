package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Trailer} object contains information related to a single movie trailer.
 */

public class Trailer implements Parcelable {

    /**
     * Converts Parcel back to the Trailer object
     */
    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    /**
     * Id of the movie
     */
    private int movieId;

    /**
     * Title of the movie trailer
     */
    private String movieTrailerTitle;

    /**
     * Link to thumbnail image of the movie trailer
     */
    private String movieTrailerThumbnail;

    /**
     * Link to the movie trailer
     */
    private String movieTrailerLink;

    /**
     * Constructs a new {@link Trailer} object.
     *
     * @param movieId               is the movie ID
     * @param movieTrailerTitle     is the title of the movie trailer
     * @param movieTrailerThumbnail is the link to movie trailer thumbnail image
     * @param movieTrailerLink      is the link to trailer movie itself
     */
    public Trailer(int movieId, String movieTrailerTitle, String movieTrailerThumbnail, String movieTrailerLink) {
        this.movieId = movieId;
        this.movieTrailerTitle = movieTrailerTitle;
        this.movieTrailerThumbnail = movieTrailerThumbnail;
        this.movieTrailerLink = movieTrailerLink;
    }

    /**
     * Retrieving Trailer object fields from the Parcel
     */
    private Trailer(Parcel in) {
        movieId = in.readInt();
        movieTrailerTitle = in.readString();
        movieTrailerThumbnail = in.readString();
        movieTrailerLink = in.readString();
    }

    /**
     * Getters for the {@link Trailer} object data.
     */
    public int getMovieId() {
        return movieId;
    }

    public String getMovieTrailerTitle() {
        return movieTrailerTitle;
    }

    public String getMovieTrailerThumbnail() {
        return movieTrailerThumbnail;
    }

    public String getMovieTrailerLink() {
        return movieTrailerLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens Trailer object into a Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTrailerTitle);
        dest.writeString(movieTrailerThumbnail);
        dest.writeString(movieTrailerLink);
    }
}
