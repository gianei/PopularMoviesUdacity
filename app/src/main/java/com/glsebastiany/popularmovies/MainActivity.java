package com.glsebastiany.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.glsebastiany.popularmovies.data.FilmsContentProviderCursorHelper;
import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Film>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private static final String SELECTED_FILTER_PREF_KEY = "selected_filter_pref_key";

    private RecyclerView mFilmsRecyclerView;
    private FilmsAdapter mFilmsAdapter;

    private TextView mErrorText;
    private ProgressBar mProgressBar;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findIds();
        setupFilmsGrid();

        mPreferences = getPreferences(MODE_PRIVATE);

        applyFilter(mPreferences.getInt(SELECTED_FILTER_PREF_KEY, R.id.menu_sort_popular));
    }

    private void fetchMovies(NetworkUtils.SortType sortType) {
        if (NetworkUtils.isConnectedToInternet(this)) {
            new FetchMoviesTask().execute(sortType);
        } else {
            setErrorState(getString(R.string.error_no_internet));
        }
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
                fetchMovies(NetworkUtils.SortType.Popular);
                return true;
            case R.id.menu_sort_top_rated:
                fetchMovies(NetworkUtils.SortType.TopRated);
                return true;
            case R.id.menu_sort_favorites:
                preFetchSetStatus();
                getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
                return true;
        }
        return false;
    }

    private void preFetchSetStatus() {
        mFilmsAdapter.setFilms(null);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
    }

    private void postLoadSetStatus(List<Film> movies) {
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

    private class FetchMoviesTask extends AsyncTask<NetworkUtils.SortType, Void, List<Film>>{

        @Override
        protected void onPreExecute() {
            preFetchSetStatus();
        }

        @Override
        protected List<Film> doInBackground(NetworkUtils.SortType... params) {
            NetworkUtils.SortType sortType;
            if (params != null && params.length > 0){
                sortType = params[0];
            } else {
                sortType = NetworkUtils.SortType.Popular;
            }

            URL url = NetworkUtils.buildUrl(sortType);

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);

                return NetworkUtils.parseJson(response);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Film> movies) {
            postLoadSetStatus(movies);
            Log.v(TAG, "Async task completed");
        }
    }

    @Override
    public Loader<List<Film>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Film>>(this) {

            List<Film> mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Film> loadInBackground() {
                try {
                    return FilmsContentProviderCursorHelper.getFilmsFromCursor(FilmsContentProviderCursorHelper.getFilmsCursor(getContext()));

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(List<Film> data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<Film>> loader, List<Film> data) {
        if (data.size() == 0){
            setErrorState(getString(R.string.error_no_favorites));
        } else {
            postLoadSetStatus(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Film>> loader) {
        postLoadSetStatus(new ArrayList<Film>());
    }

}
