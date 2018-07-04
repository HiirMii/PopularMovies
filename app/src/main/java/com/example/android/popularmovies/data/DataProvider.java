package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.ReviewContract.ReviewEntry;
import com.example.android.popularmovies.data.TrailerContract.TrailerEntry;

public class DataProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DataProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the movies table
     */
    private static final int MOVIES = 100;

    /**
     * URI matcher code for the content URI for a single disc in the movies table
     */
    private static final int MOVIES_ID = 101;

    /**
     * URI matcher code for the content URI for the trailers table
     */
    private static final int TRAILERS = 200;

    /**
     * URI matcher code for the content URI for the reviews table
     */
    private static final int REVIEWS = 300;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //URIs used to provide access to MULTIPLE rows of the tables.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(TrailerContract.CONTENT_AUTHORITY, TrailerContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(ReviewContract.CONTENT_AUTHORITY, ReviewContract.PATH_REVIEWS, REVIEWS);

        //This URI is used to provide access to ONE single row of the movies table.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_ID);
    }

    /**
     * Database helper object
     */
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIES_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRAILERS:
                cursor = database.query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEWS:
                cursor = database.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIES_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return TrailerEntry.CONTENT_LIST_TYPE;
            case REVIEWS:
                return ReviewEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return insertMovie(uri, values);
            case TRAILERS:
                return insertTrailer(uri, values);
            case REVIEWS:
                return insertReview(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new movie with the given values
        long id = database.insert(MovieEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the movie content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTrailer(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new trailer with the given values
        long id = database.insert(TrailerEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the movie content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertReview(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new review with the given values
        long id = database.insert(ReviewEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the movie content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                // Delete a single row given by the ID in the URI
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
