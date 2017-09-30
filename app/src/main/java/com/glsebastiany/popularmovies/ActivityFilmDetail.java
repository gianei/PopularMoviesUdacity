package com.glsebastiany.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.glsebastiany.popularmovies.data.DatabaseContract;
import com.glsebastiany.popularmovies.data.FilmsContentProviderCursorHelper;
import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.model.Review;
import com.glsebastiany.popularmovies.model.Video;
import com.glsebastiany.popularmovies.util.CachedAsyncTaskLoader;
import com.glsebastiany.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class ActivityFilmDetail extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Object>{

    private static final String TAG = ActivityFilmDetail.class.getSimpleName();
    private static final String EXTRA_FILM = "EXTRA_FILM";
    private static final int TASK_CURSOR_LOADER_ID = 0;
    private static final int TASK_REVIEWS_LOADER_ID = 1;
    private static final int TASK_VIDEOS_LOADER_ID = 2;
    private static final String TASK_LOADER_ID_BUNDLE_KEY = "ID_BUNDLE_KEY";
    private static final String BUNDLE_IS_FILM_FAVORITE_IN_DB = "BUNDLE_IS_FILM_FAVORITE_IN_DB";

    private Film mFilm;

    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewVoteAverage;
    private TextView mTextViewSynopsis;
    private Switch mSwitchFavorite;
    private boolean mIsFilmFavoriteInDb = false;

    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;

    private RecyclerView mVideosRecyclerView;
    private VideosAdapter mVideosAdapter;

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
        setupReviewsList();
        setupVideosList();

        Intent intent = getIntent();

        if (savedInstanceState!= null && savedInstanceState.containsKey(BUNDLE_IS_FILM_FAVORITE_IN_DB)){
            mIsFilmFavoriteInDb = savedInstanceState.getBoolean(BUNDLE_IS_FILM_FAVORITE_IN_DB);
        }

        if (intent.hasExtra(EXTRA_FILM)) {
            mFilm = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_FILM));
            bindView();
            setupSwitchListener();

            Bundle args = new Bundle();
            args.putString(TASK_LOADER_ID_BUNDLE_KEY, Integer.toString(mFilm.getId()));

            getSupportLoaderManager().initLoader(TASK_REVIEWS_LOADER_ID, args, this);
            getSupportLoaderManager().initLoader(TASK_VIDEOS_LOADER_ID, args, this);
            getSupportLoaderManager().restartLoader(TASK_CURSOR_LOADER_ID, args, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_IS_FILM_FAVORITE_IN_DB, mIsFilmFavoriteInDb);
        super.onSaveInstanceState(outState);
    }

    private void findIds() {
        mTextViewTitle = findViewById(R.id.tv_title);
        mImageViewPoster = findViewById(R.id.iv_film_poster);
        mTextViewReleaseDate = findViewById(R.id.tv_release_date);
        mTextViewVoteAverage = findViewById(R.id.tv_vote_average);
        mTextViewSynopsis = findViewById(R.id.tv_synopsis);
        mSwitchFavorite = findViewById(R.id.sw_favorite);
        mReviewsRecyclerView = findViewById(R.id.rv_reviews);
        mVideosRecyclerView = findViewById(R.id.rv_videos);
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
                if (isChecked == mIsFilmFavoriteInDb){
                    return;
                }

                mIsFilmFavoriteInDb = isChecked;

                if (mIsFilmFavoriteInDb){
                    FilmsContentProviderCursorHelper.addFavoriteMovie(ActivityFilmDetail.this, mFilm);
                } else {
                    FilmsContentProviderCursorHelper.removeFavoriteMovie(ActivityFilmDetail.this, Integer.toString(mFilm.getId()));
                }

            }
        });
    }

    private void setFavoriteSwitchFromCursor(Cursor data) {
        if (data != null && data.moveToFirst()){
            mIsFilmFavoriteInDb = true;
            mSwitchFavorite.setChecked(true);
        } else {
            mIsFilmFavoriteInDb = false;
            mSwitchFavorite.setChecked(false);
        }
    }

    private void setupReviewsList() {
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    private void setupVideosList() {
        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVideosAdapter = new VideosAdapter(new VideosAdapter.VideoClickListener() {
            @Override
            public void onFilmClick(Video video) {
                video.open(ActivityFilmDetail.this);
            }
        });
        mVideosRecyclerView.setAdapter(mVideosAdapter);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, final Bundle loaderArgs) {
        final String filmId = loaderArgs.getString(TASK_LOADER_ID_BUNDLE_KEY);
        switch (id) {

            case TASK_REVIEWS_LOADER_ID:
                return new CachedAsyncTaskLoader<Object>(this) {
                    @Override
                    public Object internalLoadInBackground() throws Exception {

                        URL url = NetworkUtils.buildFilmDetailUrl(NetworkUtils.DetailType.Reviews, filmId);

                        try {
                            String response = NetworkUtils.getResponseFromHttpUrl(url);

                            return NetworkUtils.parseReviewsListJson(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };

            case TASK_VIDEOS_LOADER_ID:
                return new CachedAsyncTaskLoader<Object>(this) {
                    @Override
                    public Object internalLoadInBackground() throws Exception {

                        URL url = NetworkUtils.buildFilmDetailUrl(NetworkUtils.DetailType.Videos, filmId);

                        try {
                            String response = NetworkUtils.getResponseFromHttpUrl(url);

                            return NetworkUtils.parseVideosListJson(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };

            case TASK_CURSOR_LOADER_ID:
                return (Loader) new CursorLoader(this, DatabaseContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(filmId).build(), null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()){
            case TASK_REVIEWS_LOADER_ID:
                mReviewsAdapter.setReviews((List<Review>) data);
                break;
            case TASK_VIDEOS_LOADER_ID:
                mVideosAdapter.setVideos((List<Video>) data);
                break;
            case TASK_CURSOR_LOADER_ID:
                setFavoriteSwitchFromCursor((Cursor) data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()){
            case TASK_REVIEWS_LOADER_ID:
                mReviewsAdapter.setReviews(null);
            case TASK_VIDEOS_LOADER_ID:
                mVideosAdapter.setVideos(null);
            case TASK_CURSOR_LOADER_ID:
                setFavoriteSwitchFromCursor(null);
                break;
        }
    }
}
