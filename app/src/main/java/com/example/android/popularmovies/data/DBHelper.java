package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.TrailerContract.TrailerEntry;
import com.example.android.popularmovies.data.ReviewContract.ReviewEntry;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "data.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DBHelper}.
     *
     * @param context of the app
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the movies table
        String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL);";

        // Create a String that contains the SQL statement to create the movies table
        String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " ("
                + TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + TrailerEntry.COLUMN_TRAILER_TITLE + " TEXT, "
                + TrailerEntry.COLUMN_TRAILER_THUMBNAIL + " TEXT, "
                + TrailerEntry.COLUMN_TRAILER_LINK + " TEXT);";

        // Create a String that contains the SQL statement to create the movies table
        String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ("
                + ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT, "
                + ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT);";

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Create a String that contains the SQL statement to drop the movies table
        String SQL_DELETE_MOVIES_ENTRIES = "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

        // Create a String that contains the SQL statement to drop the trailers table
        String SQL_DELETE_TRAILERS_ENTRIES = "DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME;

        // Create a String that contains the SQL statement to drop the reviews table
        String SQL_DELETE_REVIEWS_ENTRIES = "DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME;

        // Execute the SQL statement to delete current tables
        db.execSQL(SQL_DELETE_MOVIES_ENTRIES);
        db.execSQL(SQL_DELETE_TRAILERS_ENTRIES);
        db.execSQL(SQL_DELETE_REVIEWS_ENTRIES);

        // Create new tables
        onCreate(db);
    }

    /**
     * Helper method used to add Trailers to database.
     */
    public void addTrailers(ArrayList<Trailer> trailerArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Trailer trailer : trailerArrayList) {
                values.put(TrailerEntry.COLUMN_MOVIE_ID, trailer.getMovieId());
                values.put(TrailerEntry.COLUMN_TRAILER_TITLE, trailer.getMovieTrailerTitle());
                values.put(TrailerEntry.COLUMN_TRAILER_THUMBNAIL, trailer.getMovieTrailerThumbnail());
                values.put(TrailerEntry.COLUMN_TRAILER_LINK, trailer.getMovieTrailerLink());
                db.insert(TrailerEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Helper method used to add Reviews to database.
     */
    public void addReviews(ArrayList<Review> reviewArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Review review : reviewArrayList) {
                values.put(ReviewEntry.COLUMN_MOVIE_ID, review.getMovieId());
                values.put(ReviewEntry.COLUMN_REVIEW_AUTHOR, review.getReviewAuthor());
                values.put(ReviewEntry.COLUMN_REVIEW_CONTENT, review.getReviewContent());
                db.insert(ReviewEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
