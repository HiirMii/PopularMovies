package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.ReviewContract.ReviewEntry;
import com.example.android.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * An {@link ReviewAdapter} knows how to create a list item layout for each movie review
 * in the data source (a list of {@link Review} objects).
 * <p>
 * These list item layouts will be provided to an adapter view (RecyclerView)
 * to be displayed to the user.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviewsList;
    private Context context;

    /**
     * Creates a ReviewAdapter.
     */
    public ReviewAdapter(ArrayList<Review> reviews) {
        this.reviewsList = reviews;
    }

    /**
     * Create new views (invoked by the layout manager).
     */
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review currentReview = reviewsList.get(position);
        holder.tvReviewAuthor.setText(currentReview.getReviewAuthor());
        holder.tvReviewContent.setText(currentReview.getReviewContent());
    }

    /**
     * Returns the size of the list.
     */
    @Override
    public int getItemCount() {
        if (reviewsList == null) return 0;
        return reviewsList.size();
    }

    /**
     * Used to populate the RecyclerView with the movie reviews list from the internet
     */
    public void setReviewsList(ArrayList<Review> reviewsList) {
        this.reviewsList = new ArrayList<>();
        this.reviewsList.clear();
        this.reviewsList.addAll(reviewsList);
        notifyDataSetChanged();
    }

    /**
     * Used to populate the RecyclerView with the movie trailers list from the database
     */
    public void setReviewsListFromCursor(Cursor cursor) {
        this.reviewsList = new ArrayList<>();
        this.reviewsList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int movieId = cursor.getInt(cursor.getColumnIndex(ReviewEntry.COLUMN_MOVIE_ID));
                String movieReviewAuthor = cursor.getString(
                        cursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_AUTHOR));
                String movieReviewContent = cursor.getString(
                        cursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_CONTENT));

                Review review = new Review(movieId, movieReviewAuthor, movieReviewContent);
                this.reviewsList.add(review);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvReviewAuthor;
        public final TextView tvReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvReviewAuthor = itemView.findViewById(R.id.tv_review_author);
            tvReviewContent = itemView.findViewById(R.id.tv_review_content);
        }
    }
}
