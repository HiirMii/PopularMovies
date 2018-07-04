package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ReviewContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REVIEWS = "reviews";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ReviewContract() {
    }

    public static final class ReviewEntry implements BaseColumns {

        /**
         * The content URI to access the review data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REVIEWS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of reviews.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        /**
         * Name of database table for reviews
         */
        public static final String TABLE_NAME = "reviews";

        /**
         * Unique ID number for the review (only for use in the database table).
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
         * Author of the movie review.
         *
         * Type: TEXT
         */
        public static final String COLUMN_REVIEW_AUTHOR = "author";

        /**
         * Content of the movie review.
         *
         * Type: TEXT
         */
        public static final String COLUMN_REVIEW_CONTENT = "content";
    }
}
