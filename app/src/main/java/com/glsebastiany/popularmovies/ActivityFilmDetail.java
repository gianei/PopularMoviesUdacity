package com.glsebastiany.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.Locale;

public class ActivityFilmDetail extends AppCompatActivity {

    private static final String EXTRA_FILM = "EXTRA_FILM";

    private Film mFilm;

    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewVoteAverage;
    private TextView mTextViewSynopsis;

    public static void startActivity(Context context, Film film){
        Intent intent = new Intent(context, ActivityFilmDetail.class);
        intent.putExtra(EXTRA_FILM, Parcels.wrap(film));

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        findIds();

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_FILM)) {
            mFilm = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_FILM));
            bindView();
        }
    }

    private void findIds() {
        mTextViewTitle = findViewById(R.id.tv_title);
        mImageViewPoster = findViewById(R.id.iv_film_poster);
        mTextViewReleaseDate = findViewById(R.id.tv_release_date);
        mTextViewVoteAverage = findViewById(R.id.tv_vote_average);
        mTextViewSynopsis = findViewById(R.id.tv_synopsis);
    }

    private void bindView() {
        mTextViewTitle.setText(mFilm.getTitle());

        Picasso
                .with(this)
                .load(NetworkUtils.getPosterUri(mFilm))
                .into(mImageViewPoster);

        mTextViewReleaseDate.setText(mFilm.getReleaseDate());
        mTextViewVoteAverage.setText(String.format(Locale.getDefault(), "%1$.1f", mFilm.getVoteAverage()));
        mTextViewSynopsis.setText(mFilm.getOverview());
    }
}
