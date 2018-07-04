package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Movie} object contains information related to a single movie.
 */

public class Movie implements Parcelable {

    /**
     * Converts Parcel back to the Movie object
     */
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * ID of the movie
     */
    private int movieId;
    /**
     * Title of the movie
     */
    private String title;
    /**
     * Relative link to the movies poster image
     */
    private String posterPath;
    /**
     * Relative link to the movies backdrop image
     */
    private String backdropPath;
    /**
     * Release date of the movie
     */
    private String releaseDate;
    /**
     * Average result of vote rating for the movie
     */
    private Double voteAverage;
    /**
     * Short overview of the movie
     */
    private String overview;

    /**
     * Constructs a new {@link Movie} object.
     *
     * @param movieId      is the ID of the movie
     * @param title        is the title of the movie
     * @param posterPath   is the relative link to the movies poster image
     * @param backdropPath is the relative link to the movies backdrop image
     * @param releaseDate  is the release date of the movie
     * @param voteAverage  is the average result of vote rating for the movie
     * @param overview     is a short overview of the movie
     */
    public Movie(int movieId, String title, String posterPath, String backdropPath,
                 String releaseDate, Double voteAverage, String overview) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    /**
     * Retrieving Movie object fields from the Parcel
     */
    private Movie(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
    }

    /**
     * Getters for the {@link Movie} object data.
     */
    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens Movie object into a Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
    }
}
