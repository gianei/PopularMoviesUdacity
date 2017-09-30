package com.glsebastiany.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glsebastiany.popularmovies.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (reviews == null){
            return 0;
        }

        return reviews.size();
    }

    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView reviewText;
        private final View itemView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            this.reviewText = itemView.findViewById(R.id.tv_review);
        }

        public void bind(){
            reviewText.setText(reviews.get(getAdapterPosition()).getContent());
        }
    }
}
