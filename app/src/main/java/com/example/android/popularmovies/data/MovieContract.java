package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        /**
         * The content URI to access the movie data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /**
         * Name of database table for movies
         */
        public static final String TABLE_NAME = "movies";

        /**
         * Unique ID number for the movie (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Unique ID number for the movie (used in TMDB API).
         *
         * Type: INTEGER
         */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /**
         * Title of the movie.
         *
         * Type: TEXT
         */
        public static final String COLUMN_MOVIE_TITLE = "title";

        /**
         * Path to to the movies poster image stored locally.
         *
         * Type: TEXT
         */
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        /**
         * Path to to the movies backdrop image stored locally.
         *
         * Type: TEXT
         */
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";

        /**
         * Release date of the movie.
         *
         * Type: TEXT
         */
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        /**
         * Average result of vote rating for the movie.
         *
         * Type: REAL
         */
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";

        /**
         * Short overview of the movie.
         *
         * Type: TEXT
         */
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
    }
}
