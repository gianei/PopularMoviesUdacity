package com.glsebastiany.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.glsebastiany.popularmovies.data.FilmsContentProviderCursorHelper;
import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.CachedAsyncTaskLoader;
import com.glsebastiany.popularmovies.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Film>>{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_FAVORITES_LOADER_ID = 0;
    private static final int TASK_FETCH_POPULAR_LOADER_ID = 1;
    private static final int TASK_FETCH_TOP_RATED_LOADER_ID = 2;
    private static final String SELECTED_FILTER_PREF_KEY = "selected_filter_pref_key";
    private static final String BUNDLE_RECYCLER_LAYOUT = "bundle_recycler_layout";

    private RecyclerView mFilmsRecyclerView;
    private FilmsAdapter mFilmsAdapter;
    private Parcelable mSavedRecyclerLayoutState;

    private TextView mErrorText;
    private ProgressBar mProgressBar;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findIds();
        setupFilmsGrid();

        if(savedInstanceState != null)
        {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }

        mPreferences = getPreferences(MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilter(mPreferences.getInt(SELECTED_FILTER_PREF_KEY, R.id.menu_sort_popular));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mFilmsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void findIds() {
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mFilmsRecyclerView = findViewById(R.id.rv_films);
        mErrorText = findViewById(R.id.tv_error_message);
    }

    private void setupFilmsGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mFilmsRecyclerView.setLayoutManager(layoutManager);

        mFilmsAdapter = new FilmsAdapter(new FilmsAdapter.FilmClickListener() {
            @Override
            public void onFilmClick(Film film) {
                ActivityFilmDetail.startActivity(MainActivity.this, film);
            }
        });
        mFilmsRecyclerView.setAdapter(mFilmsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.films_grid_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        mPreferences
                .edit()
                .putInt(SELECTED_FILTER_PREF_KEY, menuItemId)
                .apply();
        return applyFilter(menuItemId) || super.onOptionsItemSelected(item);
    }

    private boolean applyFilter(int menuItemId) {
        switch (menuItemId){
            case R.id.menu_sort_popular:
                getSupportLoaderManager().restartLoader(TASK_FETCH_POPULAR_LOADER_ID, null, this);
                return true;
            case R.id.menu_sort_top_rated:
                getSupportLoaderManager().restartLoader(TASK_FETCH_TOP_RATED_LOADER_ID, null, this);
                return true;
            case R.id.menu_sort_favorites:
                getSupportLoaderManager().initLoader(TASK_FAVORITES_LOADER_ID, null, this);
                return true;
        }
        return false;
    }

    private void preLoadSetStatus() {
        mFilmsAdapter.setFilms(null);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
    }

    private void postLoadSetStatus(List<Film> movies) {
        if (mSavedRecyclerLayoutState != null){
            mFilmsRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
            mSavedRecyclerLayoutState = null;
        }
        mFilmsAdapter.setFilms(movies);
        mProgressBar.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
    }

    private void setErrorState(String errorMessage) {
        mFilmsAdapter.setFilms(null);
        mProgressBar.setVisibility(View.GONE);
        mErrorText.setText(errorMessage);
        mErrorText.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Film>> onCreateLoader(final int id, final Bundle loaderArgs) {
        switch (id) {
            case TASK_FETCH_POPULAR_LOADER_ID:
            case TASK_FETCH_TOP_RATED_LOADER_ID:
                return new CachedAsyncTaskLoader<List<Film>>(this) {

                    @Override
                    protected void onStartLoading() {
                        preLoadSetStatus();
                        if (NetworkUtils.isConnectedToInternet(getContext())){
                            super.onStartLoading();
                        } else {
                            setErrorState(getString(R.string.error_no_internet));
                            deliverCancellation();
                        }
                    }

                    @Override
                    public List<Film> internalLoadInBackground() throws Exception {

                        NetworkUtils.SortType sortType;
                        if (id == TASK_FETCH_POPULAR_LOADER_ID ){
                            sortType = NetworkUtils.SortType.Popular;
                        } else {
                            sortType = NetworkUtils.SortType.TopRated;
                        }

                        String response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildFilmsUrl(sortType));

                        return NetworkUtils.parseFilmsListJson(response);
                    }

                };

            case TASK_FAVORITES_LOADER_ID:
                return new CachedAsyncTaskLoader<List<Film>>(this) {
                    @Override
                    protected void onStartLoading() {
                        preLoadSetStatus();
                        super.onStartLoading();
                    }

                    @Override
                    public List<Film> internalLoadInBackground() throws Exception {
                        return FilmsContentProviderCursorHelper.getFilmsFromCursor(FilmsContentProviderCursorHelper.getFilmsCursor(getContext()));
                    }

                };

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Film>> loader, List<Film> data) {
        switch (loader.getId()) {
            case TASK_FETCH_POPULAR_LOADER_ID:
            case TASK_FETCH_TOP_RATED_LOADER_ID:
                postLoadSetStatus(data);
                break;

            case TASK_FAVORITES_LOADER_ID:
                if (data.size() == 0) {
                    setErrorState(getString(R.string.error_no_favorites));
                } else {
                    postLoadSetStatus(data);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Film>> loader) {
        switch (loader.getId()) {
            case TASK_FETCH_POPULAR_LOADER_ID:
            case TASK_FETCH_TOP_RATED_LOADER_ID:
            case TASK_FAVORITES_LOADER_ID:
                postLoadSetStatus(new ArrayList<Film>());
                break;
        }
    }

}
