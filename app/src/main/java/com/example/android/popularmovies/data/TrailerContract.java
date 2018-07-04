package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TrailerContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRAILERS = "trailers";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TrailerContract() {
    }

    public static final class TrailerEntry implements BaseColumns {

        /**
         * The content URI to access the trailer data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRAILERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trailers.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        /**
         * Name of database table for trailers
         */
        public static final String TABLE_NAME = "trailers";

        /**
         * Unique ID number for the trailer (only for use in the database table).
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
         * Title of the trailer.
         *
         * Type: TEXT
         */
        public static final String COLUMN_TRAILER_TITLE = "title";

        /**
         * Name of the file containing movie trailer thumbnail.
         *
         * Type: TEXT
         */
        public static final String COLUMN_TRAILER_THUMBNAIL = "thumbnail";

        /**
         * Link to the movie trailer.
         *
         * Type: TEXT
         */
        public static final String COLUMN_TRAILER_LINK = "link";
    }
}
