package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.DBHelper;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.ReviewContract.ReviewEntry;
import com.example.android.popularmovies.data.TrailerContract.TrailerEntry;
import com.example.android.popularmovies.loader.AsyncTaskLoaderOnStartListener;
import com.example.android.popularmovies.loader.ReviewLoader;
import com.example.android.popularmovies.loader.TrailerLoader;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    /**
     * Constant value for the trailer loader ID.
     */
    private static final int TRAILER_LOADER_ID = 0;
    /**
     * Constant value for the review loader ID.
     */
    private static final int REVIEW_LOADER_ID = 1;

    Toolbar toolbar;

    Movie movie;
    TextView movieTitle;
    ImageView moviePoster;
    ImageView movieBackdrop;
    TextView movieReleaseDate;
    TextView movieVoteAverage;
    RatingBar ratingBar;
    TextView movieOverview;
    /**
     * Stores movie ID currently used by loader manager.
     */
    Bundle queryMovieId = new Bundle();
    /**
     * List of trailers
     */
    ArrayList<Trailer> simpleJsonTrailersData;
    /**
     * List of reviews
     */
    ArrayList<Review> simpleJsonReviewsData;
    /**
     * FAB used to add movie to favourites list
     */
    FloatingActionButton addFab;
    /**
     * FAB used to remove movie from favourites list
     */
    FloatingActionButton removeFab;
    /**
     * Recycler view used to display the list of trailers.
     */
    private RecyclerView trailersRecyclerView;
    /**
     * Recycler view used to display the list of reviews.
     */
    private RecyclerView reviewsRecyclerView;
    /**
     * Adapter for the list of trailers.
     */
    private TrailerAdapter trailerAdapter;
    /**
     * Adapter for the list of reviews.
     */
    private ReviewAdapter reviewAdapter;
    /**
     * Layout used to display trailer items.
     */
    private LinearLayoutManager trailerLayoutManager;
    /**
     * Layout used to display review items.
     */
    private LinearLayoutManager reviewLayoutManager;
    /**
     * TextView that is displayed when the trailers list is empty
     */
    private TextView trailersErrorMessageDisplay;
    /**
     * TextView that is displayed when the reviews list is empty
     */
    private TextView reviewsErrorMessageDisplay;
    /**
     * ProgressBar that is displayed when the trailers data is being loaded
     */
    private ProgressBar trailersLoadingIndicator;
    /**
     * ProgressBar that is displayed when the reviews data is being loaded
     */
    private ProgressBar reviewsLoadingIndicator;
    /**
     * Used to load reviews data from database.
     */
    LoaderManager.LoaderCallbacks<Cursor> cursorReviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

            String[] projection = {
                    ReviewEntry._ID,
                    ReviewEntry.COLUMN_MOVIE_ID,
                    ReviewEntry.COLUMN_REVIEW_AUTHOR,
                    ReviewEntry.COLUMN_REVIEW_CONTENT
            };

            String selection = ReviewEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId();

            return new CursorLoader(DetailsActivity.this,
                    ReviewEntry.CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            if (cursor == null || cursor.getCount() == 0) {
                showReviewsErrorMessage(getString(R.string.error_message_no_reviews));
            } else {
                showReviewsDataView();
                reviewAdapter.setReviewsListFromCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };
    /**
     * Used to load trailers data from TMDB API.
     */
    private LoaderManager.LoaderCallbacks<ArrayList<Trailer>> asyncTrailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<Trailer>>() {
        @Override
        public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {

            final String currentMovieId = args.getString("movieId");

            return new TrailerLoader(DetailsActivity.this, currentMovieId, new TrailerLoaderOnStartListener());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
            trailersLoadingIndicator.setVisibility(View.INVISIBLE);
            simpleJsonTrailersData = data;
            trailerAdapter.setTrailersList(data);
            if (data == null) {
                showTrailersErrorMessage(getString(R.string.error_message_no_internet_title));
            } else if (data.size() == 0) {
                showTrailersErrorMessage(getString(R.string.error_message_no_trailers));
            } else {
                showTrailersDataView();
            }
            // update menu
            invalidateOptionsMenu();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {

        }
    };
    /**
     * Used to load trailers data from database.
     */
    private LoaderManager.LoaderCallbacks<Cursor> cursorTrailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

            String[] projection = {
                    TrailerEntry._ID,
                    TrailerEntry.COLUMN_MOVIE_ID,
                    TrailerEntry.COLUMN_TRAILER_TITLE,
                    TrailerEntry.COLUMN_TRAILER_THUMBNAIL,
                    TrailerEntry.COLUMN_TRAILER_LINK
            };

            String selection = TrailerEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId();

            return new CursorLoader(DetailsActivity.this,
                    TrailerEntry.CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            trailersLoadingIndicator.setVisibility(View.INVISIBLE);
            if (cursor == null || cursor.getCount() == 0) {
                showTrailersErrorMessage(getString(R.string.error_message_no_trailers));
            } else {
                showTrailersDataView();
                trailerAdapter.setTrailersListFromCursor(cursor);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };
    /**
     * Used to load reviews data from TMDB API.
     */
    private LoaderManager.LoaderCallbacks<ArrayList<Review>> asyncReviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {

            final String currentMovieId = args.getString("movieId");

            return new ReviewLoader(DetailsActivity.this, currentMovieId, new ReviewLoaderOnStartListener());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
            reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            simpleJsonReviewsData = data;
            reviewAdapter.setReviewsList(simpleJsonReviewsData);
            if (data == null) {
                showReviewsErrorMessage(getString(R.string.error_message_no_internet_title));
            } else if (data.size() == 0) {
                showReviewsErrorMessage(getString(R.string.error_message_no_reviews));
            } else {
                showReviewsDataView();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = findViewById(R.id.tb_details);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        movieTitle = findViewById(R.id.tv_title);
        moviePoster = findViewById(R.id.iv_movie_poster);
        movieBackdrop = findViewById(R.id.iv_backdrop);
        movieReleaseDate = findViewById(R.id.tv_release_date_data);
        movieVoteAverage = findViewById(R.id.tv_rating_data);
        ratingBar = findViewById(R.id.rb_movie_rating);
        movieOverview = findViewById(R.id.tv_overview);

        addFab = findViewById(R.id.fab_add);
        removeFab = findViewById(R.id.fab_remove);

        if (getIntent() != null && getIntent().getExtras() != null) {

            movie = getIntent().getExtras().getParcelable("movie");

            queryMovieId.putString("movieId", String.valueOf(movie.getMovieId()));

            Picasso.with(this)
                    .load(movie.getBackdropPath())
                    .into(movieBackdrop);

            getSupportActionBar().setTitle(movie.getTitle());

            Picasso.with(this)
                    .load(movie.getPosterPath())
                    .into(moviePoster);

            movieTitle.setText(movie.getTitle());

            movieReleaseDate.setText(formatDate(movie.getReleaseDate()));

            String movieRating = Double.toString(movie.getVoteAverage()) + "/10";
            movieVoteAverage.setText(movieRating);

            ratingBar.setRating(movie.getVoteAverage().floatValue() / 2);

            movieOverview.setText(movie.getOverview());
        }

        trailersRecyclerView = findViewById(R.id.rv_trailers);

        reviewsRecyclerView = findViewById(R.id.rv_reviews);

        trailersErrorMessageDisplay = findViewById(R.id.tv_trailers_error_message_display);

        reviewsErrorMessageDisplay = findViewById(R.id.tv_reviews_error_message_display);

        trailerLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        reviewLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        trailersRecyclerView.setLayoutManager(trailerLayoutManager);

        trailersRecyclerView.setHasFixedSize(true);

        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        reviewsRecyclerView.setHasFixedSize(true);

        trailerAdapter =
                new TrailerAdapter(simpleJsonTrailersData, new TrailerAdapter.TrailerAdapterOnClickHandler() {
                    @Override
                    public void onClick(Trailer trailer) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(trailer.getMovieTrailerLink()));
                        startActivity(intent);
                    }
                });

        trailersRecyclerView.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(simpleJsonReviewsData);

        reviewsRecyclerView.setAdapter(reviewAdapter);

        trailersLoadingIndicator = findViewById(R.id.pb_trailers_loading_indicator);

        reviewsLoadingIndicator = findViewById(R.id.pb_reviews_loading_indicator);

        if (isFavourite(queryMovieId.getString("movieId"))) {
            removeFab.setClickable(true);
            addFab.setVisibility(View.INVISIBLE);
            addFab.setClickable(false);

            loadTrailersData(cursorTrailerLoaderCallbacks);
            loadReviewsData(cursorReviewLoaderCallbacks);
        } else {
            addFab.setClickable(true);
            removeFab.setVisibility(View.INVISIBLE);
            removeFab.setClickable(false);

            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey("trailers")) {
                    simpleJsonTrailersData = savedInstanceState.getParcelableArrayList("trailers");
                    if (simpleJsonTrailersData.size() != 0) {
                        trailerAdapter.setTrailersList(simpleJsonTrailersData);
                    } else {
                        showTrailersErrorMessage(getString(R.string.error_message_no_trailers));
                    }
                }
                if (savedInstanceState.containsKey("reviews")) {
                    simpleJsonReviewsData = savedInstanceState.getParcelableArrayList("reviews");
                    if (simpleJsonReviewsData.size() != 0) {
                        reviewAdapter.setReviewsList(simpleJsonReviewsData);
                    } else {
                        showReviewsErrorMessage(getString(R.string.error_message_no_reviews));
                    }
                }
            } else if (isConnected()) {
                loadTrailersData(asyncTrailerLoaderCallbacks);
                loadReviewsData(asyncReviewLoaderCallbacks);
            } else {
                showTrailersErrorMessage(getString(R.string.error_message_no_internet_title));
                showReviewsErrorMessage(getString(R.string.error_message_no_internet_title));
            }
        }

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavourites();
                addFab.setClickable(false);
                addFab.setVisibility(View.INVISIBLE);
                removeFab.setClickable(true);
                removeFab.setVisibility(View.VISIBLE);
            }
        });

        removeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromFavourites();
                addFab.setClickable(true);
                addFab.setVisibility(View.VISIBLE);
                removeFab.setClickable(false);
                removeFab.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Saves current trailer and review lists in Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers", simpleJsonTrailersData);
        outState.putParcelableArrayList("reviews", simpleJsonReviewsData);
        super.onSaveInstanceState(outState);
    }

    /**
     * Starts Trailers AsyncTask
     */
    private void loadTrailersData(LoaderManager.LoaderCallbacks callbacks) {
        showTrailersDataView();

        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, queryMovieId, callbacks);
    }

    /**
     * Starts Reviews AsyncTask
     */
    private void loadReviewsData(LoaderManager.LoaderCallbacks callbacks) {
        showReviewsDataView();

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, queryMovieId, callbacks);
    }

    /**
     * Displays trailers list and hides error message
     */
    private void showTrailersDataView() {
        trailersErrorMessageDisplay.setVisibility(View.INVISIBLE);

        trailersRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays reviews list and hides error message
     */
    private void showReviewsDataView() {
        reviewsErrorMessageDisplay.setVisibility(View.INVISIBLE);

        reviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays error message and hides empty trailers list
     */
    private void showTrailersErrorMessage(String errorText) {
        trailersRecyclerView.setVisibility(View.INVISIBLE);

        trailersErrorMessageDisplay.setText(errorText);
        trailersErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Displays error message and hides empty reviews list
     */
    private void showReviewsErrorMessage(String errorText) {
        reviewsRecyclerView.setVisibility(View.INVISIBLE);

        reviewsErrorMessageDisplay.setText(errorText);
        reviewsErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Formats date to desired output.
     */
    private String formatDate(String dateObject) {

        String date = null;

        try {
            Date object = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateObject);
            date = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(object);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu share options from the res/menu/details.xml file.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // This adds menu share item if trailer list does not exist or is empty.
        if (simpleJsonTrailersData == null || simpleJsonTrailersData.size() == 0) {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                String trailerLink = simpleJsonTrailersData.get(0).getMovieTrailerLink();
                shareTrailerLink(trailerLink);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method used to share movie trailer
     */
    private void shareTrailerLink(String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, key);
        startActivity(shareIntent);
    }

    /**
     * Helper method to check if the movie is stored in fav list
     */
    private boolean isFavourite(String movieId) {
        Cursor favCursor = this.getContentResolver().query(MovieEntry.CONTENT_URI,
                null, MovieEntry.COLUMN_MOVIE_ID + " = " + movieId,
                null, null);
        if (favCursor != null && favCursor.moveToFirst()) {
            favCursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method used to add movie details, trailers and reviews to favourites list
     */
    private void addToFavourites() {
        addMovie(movie);
        DBHelper helper = new DBHelper(this);
        helper.addTrailers(simpleJsonTrailersData);
        helper.addReviews(simpleJsonReviewsData);
    }

    /**
     * Adds movie details to database and informs client about the progress via toast message
     */
    private void addMovie(Movie movie) {
        int movieId = movie.getMovieId();
        String movieTitle = movie.getTitle();
        String posterPath = movie.getPosterPath();
        String backdropPath = movie.getBackdropPath();
        String releaseDate = movie.getReleaseDate();
        Double voteAverage = movie.getVoteAverage();
        String overview = movie.getOverview();

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
        values.put(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
        values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);

        Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(this, getString(R.string.add_movie_error_beginning) + " "
                    + movieTitle + " " + getString(R.string.add_movie_error_ending), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, movieTitle + " " + getString(R.string.movie_added),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method used to remove movie details, trailers and reviews from favourites list
     */
    private void removeFromFavourites() {
        removeMovie(movie);
        removeTrailers(movie);
        removeReviews(movie);
        finish();
    }

    /**
     * Removes movie details from database and informs client about the progress via toast message
     */
    private void removeMovie(Movie movie) {
        String movieTitle = movie.getTitle();
        int rowsDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId(), null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.remove_movie_error_beginning) + " "
                    + movieTitle + " " + getString(R.string.remove_movie_error_ending), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, movieTitle + " " + getString(R.string.movie_removed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Removes trailers of given movie from the database
     */
    private void removeTrailers(Movie movie) {
        getContentResolver().delete(TrailerEntry.CONTENT_URI,
                TrailerEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId(), null);
    }

    /**
     * Removes reviews of given movie from the database
     */
    private void removeReviews(Movie movie) {
        getContentResolver().delete(ReviewEntry.CONTENT_URI,
                ReviewEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId(), null);
    }

    public class TrailerLoaderOnStartListener implements AsyncTaskLoaderOnStartListener {

        @Override
        public void showProgressBar() {
            trailersLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    public class ReviewLoaderOnStartListener implements AsyncTaskLoaderOnStartListener {

        @Override
        public void showProgressBar() {
            reviewsLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }
}
