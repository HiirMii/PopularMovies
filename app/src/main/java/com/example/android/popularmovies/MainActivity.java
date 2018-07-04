package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.loader.AsyncTaskLoaderOnStartListener;
import com.example.android.popularmovies.loader.MovieLoader;
import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler {

    /**
     * Possible error types
     */
    private static final String NO_INTERNET = "no_internet";
    private static final String EMPTY_CURSOR = "empty_cursor";
    /**
     * Constant value for the movie loader ID.
     */
    private static final int ASYNC_MOVIE_LOADER_ID = 0;
    /**
     * Constant value for the cursor loader ID.
     */
    private static final int CURSOR_MOVIE_LOADER_ID = 0;
    /**
     * Initial value for the query page parameter.
     */
    private static final String PAGE = "1";
    /**
     * These Strings are keywords for TMDB url queries.
     */
    private final String POPULAR_MOVIES_KEYWORD = "popular";
    private final String TOP_RATED_MOVIES_KEYWORD = "top_rated";
    private final String FAVOURITE_MOVIES_KEYWORD = "favourites";
    /**
     * Stores keyword currently used by loader manager.
     */
    Bundle queryParameters = new Bundle();
    /**
     * List of movies used to send data to adapter in case of choosing same sorting type(not favourites)
     */
    ArrayList<Movie> simpleJsonMoviesData;
    /**
     * Cursor used to send data to adapter in case of choosing same sorting type(favourites)
     */
    Cursor cursorData;
    /**
     * Recycler view used to display the list of movies.
     */
    private RecyclerView recyclerView;
    /**
     * Adapter for the list of movies.
     */
    private MovieAdapter movieAdapter;
    /**
     * Layout used to display movie items as the grid.
     */
    private GridLayoutManager layoutManager;
    /**
     * Imageview that displays error image when the list is empty
     */
    private ImageView errorImage;
    /**
     * TextView that displays error message title when the list is empty
     */
    private TextView errorMessageTitle;
    /**
     * TextView that displays error message subtitle when the list is empty
     */
    private TextView errorMessageSubtitle;
    /**
     * ProgressBar that is displayed when the data is loaded
     */
    private ProgressBar loadingIndicator;
    /**
     * Used to load movies data from TMDB API.
     */
    private LoaderCallbacks<ArrayList<Movie>> asyncMovieLoaderCallbacks = new LoaderCallbacks<ArrayList<Movie>>() {

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {

            final String currentKeyword = args.getString("keyword");

            final String currentPage = args.getString("page");

            return new MovieLoader(MainActivity.this, currentKeyword, currentPage, new MovieLoaderOnStartListener());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            simpleJsonMoviesData = data;
            if (data == null) {
                showErrorMessage(NO_INTERNET);
            } else {
                showMoviesDataView();
                movieAdapter.setMoviesList(simpleJsonMoviesData);
            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

        }
    };
    /**
     * Used to load movies data from database.
     */
    private LoaderCallbacks<Cursor> cursorMovieLoaderCallbacks = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Define a projection that specifies the columns from the table we care about.
            String[] projection = {
                    MovieEntry._ID,
                    MovieEntry.COLUMN_MOVIE_ID,
                    MovieEntry.COLUMN_MOVIE_TITLE,
                    MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                    MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                    MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                    MovieEntry.COLUMN_MOVIE_OVERVIEW
            };
            return new CursorLoader(MainActivity.this,
                    MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            cursorData = cursor;
            if (cursor == null || cursor.getCount() == 0) {
                showErrorMessage(EMPTY_CURSOR);
            } else {
                showMoviesDataView();
                movieAdapter.setMoviesListFromCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMainView();

        // Restores sort mode on device orientation based on keyword saved in Bundle
        if (savedInstanceState == null || !savedInstanceState.containsKey("currentKeyword")) {
            loadMoviesData(FAVOURITE_MOVIES_KEYWORD, PAGE);
            getSupportActionBar().setSubtitle(R.string.action_favourites);
        } else {
            String currentKeyword = savedInstanceState.getString("currentKeyword");
            getSupportActionBar().setSubtitle(savedInstanceState.getString("currentSubtitle"));
            loadMoviesData(currentKeyword, PAGE);
        }
    }

    /**
     * Saves current movie keyword and subtitle for action bar in Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentKeyword", queryParameters.getString("keyword"));
        outState.putString("currentSubtitle", String.valueOf(getSupportActionBar().getSubtitle()));
        super.onSaveInstanceState(outState);
    }

    /**
     * Starts AsyncTaskLoader
     */
    private void loadMoviesData(String keyword, String page) {

        String previousKeyword = queryParameters.getString("keyword");

        queryParameters.putString("keyword", keyword);

        queryParameters.putString("page", page);

        showMoviesDataView();

        if (keyword.equals(FAVOURITE_MOVIES_KEYWORD)) {
            if (previousKeyword == null) {
                LoaderManager movieLoaderManager = getSupportLoaderManager();
                movieLoaderManager.initLoader(CURSOR_MOVIE_LOADER_ID, queryParameters, cursorMovieLoaderCallbacks);
            } else if (keyword.equals(previousKeyword) && cursorData.getCount() != 0) {
                movieAdapter.setMoviesListFromCursor(cursorData);
            } else {
                createMainView();
                LoaderManager movieLoaderManager = getSupportLoaderManager();
                movieLoaderManager.restartLoader(CURSOR_MOVIE_LOADER_ID, queryParameters, cursorMovieLoaderCallbacks);
            }
        } else if (isConnected()) {
            if (keyword.equals(previousKeyword)) {
                movieAdapter.setMoviesList(simpleJsonMoviesData);
            } else {
                createMainView();
                LoaderManager movieLoaderManager = getSupportLoaderManager();
                movieLoaderManager.restartLoader(ASYNC_MOVIE_LOADER_ID, queryParameters, asyncMovieLoaderCallbacks);
            }
        } else {
            showErrorMessage(NO_INTERNET);
        }
    }

    /**
     * Starts DetailsActivity and sends current movie item data to DetailsActivity
     */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    /**
     * Displays movie list and hides error message
     */
    private void showMoviesDataView() {
        errorImage.setVisibility(View.INVISIBLE);
        errorMessageTitle.setVisibility(View.INVISIBLE);
        errorMessageSubtitle.setVisibility(View.INVISIBLE);

        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays error message and hides empty list
     */
    private void showErrorMessage(String errorType) {
        recyclerView.setVisibility(View.INVISIBLE);

        if (errorType.equals(NO_INTERNET)) {
            errorImage.setImageResource(R.drawable.no_internet);
            errorMessageTitle.setText(getText(R.string.error_message_no_internet_title));
            errorMessageSubtitle.setText(getText(R.string.error_message_no_internet_subtitle));
        } else {
            // EMPTY_CURSOR
            errorImage.setImageResource(R.drawable.no_favourites);
            errorMessageTitle.setText(getText(R.string.error_message_no_favourites_title));
            errorMessageSubtitle.setText(getText(R.string.error_message_no_favourites_subtitle));
        }

        errorImage.setVisibility(View.VISIBLE);
        errorMessageTitle.setVisibility(View.VISIBLE);
        errorMessageSubtitle.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Most Popular Movies" menu option
            case R.id.action_popular:
                loadMoviesData(POPULAR_MOVIES_KEYWORD, PAGE);
                getSupportActionBar().setSubtitle(R.string.action_popular);
                return true;
            // Respond to a click on the "Top Rated Movies" menu option
            case R.id.action_top_rated:
                loadMoviesData(TOP_RATED_MOVIES_KEYWORD, PAGE);
                getSupportActionBar().setSubtitle(R.string.action_top_rated);
                return true;
            // Respond to a click on the "Favourite Movies" menu option
            case R.id.action_favourites:
                loadMoviesData(FAVOURITE_MOVIES_KEYWORD, PAGE);
                getSupportActionBar().setSubtitle(R.string.action_favourites);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createMainView() {

        // Find a reference to the {@link RecyclerView} in the layout
        recyclerView = findViewById(R.id.rv_movies);

        // Create gridlayout with three columns on landscape orientation or with two columns on portrait
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        // Create a new adapter that takes an empty list of movies as input
        movieAdapter = new MovieAdapter(this);

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        recyclerView.setAdapter(movieAdapter);

        // Find the reference to the error message image view
        errorImage = findViewById(R.id.iv_error_image);

        // Find the reference to the error message title view
        errorMessageTitle = findViewById(R.id.tv_error_message_title);

        // Find the reference to the error message title view
        errorMessageSubtitle = findViewById(R.id.tv_error_message_subtitle);

        // Find the reference to the progress bar in a layout
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
    }

    /**
     * Helper method to check network connection
     */
    public boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // Return network connection info as boolean value
        return (networkInfo != null && networkInfo.isConnected());
    }

    public class MovieLoaderOnStartListener implements AsyncTaskLoaderOnStartListener {

        @Override
        public void showProgressBar() {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
    }
}
