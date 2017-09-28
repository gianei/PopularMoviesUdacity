package com.glsebastiany.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.glsebastiany.popularmovies.data.FilmsContentProviderCursorHelper;
import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.Locale;

public class ActivityFilmDetail extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ActivityFilmDetail.class.getSimpleName();
    private static final String EXTRA_FILM = "EXTRA_FILM";
    private static final int TASK_LOADER_ID = 0;
    private static final String TASK_LOADER_ID_BUNDLE_KEY = "ID_BUNDLE_KEY";

    private Film mFilm;

    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewVoteAverage;
    private TextView mTextViewSynopsis;
    private Switch mSwitchFavorite;
    private boolean mIsFilmFavorite = false;

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
            setupSwitchListener();

            Bundle args = new Bundle();
            args.putString(TASK_LOADER_ID_BUNDLE_KEY, Integer.toString(mFilm.getId()));
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, args, this);
        }
    }

    private void findIds() {
        mTextViewTitle = findViewById(R.id.tv_title);
        mImageViewPoster = findViewById(R.id.iv_film_poster);
        mTextViewReleaseDate = findViewById(R.id.tv_release_date);
        mTextViewVoteAverage = findViewById(R.id.tv_vote_average);
        mTextViewSynopsis = findViewById(R.id.tv_synopsis);
        mSwitchFavorite = findViewById(R.id.sw_favorite);
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

    private void setupSwitchListener(){
        mSwitchFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == mIsFilmFavorite){
                    return;
                }

                mIsFilmFavorite = isChecked;

                if (mIsFilmFavorite){
                    FilmsContentProviderCursorHelper.addFavoriteMovie(ActivityFilmDetail.this, mFilm);
                } else {
                    FilmsContentProviderCursorHelper.removeFavoriteMovie(ActivityFilmDetail.this, Integer.toString(mFilm.getId()));
                }

            }
        });
    }

    private void setFavoriteSwitchFromCursor(Cursor data) {
        if (data != null && data.moveToFirst()){
            mIsFilmFavorite = true;
            mSwitchFavorite.setChecked(true);
        } else {
            mIsFilmFavorite = false;
            mSwitchFavorite.setChecked(false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    String filmId = loaderArgs.getString(TASK_LOADER_ID_BUNDLE_KEY);
                    return FilmsContentProviderCursorHelper.getFilmCursor(getContext(), filmId);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setFavoriteSwitchFromCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setFavoriteSwitchFromCursor(null);
    }
}
