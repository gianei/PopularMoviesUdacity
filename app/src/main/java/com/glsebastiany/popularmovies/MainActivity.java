package com.glsebastiany.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mFilmsRecyclerView;
    private FilmsAdapter mFilmsAdapter;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findIds();
        setupFilmsGrid();

        new FetchMoviesTask().execute();
    }

    private void findIds() {
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mFilmsRecyclerView = (RecyclerView) findViewById(R.id.rv_films);
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
        switch (item.getItemId()){
            case R.id.menu_sort_popular:
                new FetchMoviesTask().execute(NetworkUtils.SortType.Popular);
                return true;
            case R.id.menu_sort_top_rated:
                new FetchMoviesTask().execute(NetworkUtils.SortType.TopRated);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchMoviesTask extends AsyncTask<NetworkUtils.SortType, Void, List<Film>>{

        @Override
        protected void onPreExecute() {
            mFilmsAdapter.setFilms(null);
            mProgressBar.setVisibility(View.VISIBLE);
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
            mProgressBar.setVisibility(View.INVISIBLE);
            mFilmsAdapter.setFilms(movies);
            Log.v(TAG, "Async task completed");
        }
    }

}
