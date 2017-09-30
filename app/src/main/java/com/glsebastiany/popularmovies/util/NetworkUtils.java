package com.glsebastiany.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.glsebastiany.popularmovies.BuildConfig;
import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/";
    private static final String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String PARAM_KEY = "api_key";

    private static final String PATH_MOVIE = "movie";
    private static final String PATH_POPULAR = "popular";
    private static final String PATH_TOP_RATED = "top_rated";
    private static final String PATH_DEFAULT_WIDTH = "w500";

    private static final String PATH_REVIEWS = "reviews";
    private static final String PATH_VIDEOS = "videos";

    public enum DetailType {
        Reviews,
        Videos
    }

    public static URL buildFilmDetailUrl(DetailType detailType, String filmId){
        Uri.Builder builder = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(filmId);
        switch (detailType){
            case Reviews:
                builder.appendPath(PATH_REVIEWS);
                break;
            case Videos:
                builder.appendPath(PATH_VIDEOS);
                break;
        }

        return tryBuildUrl(builder);
    }

    public enum SortType {
        Popular,
        TopRated
    }

    public static URL buildFilmsUrl(SortType sortType) {
        Uri.Builder builder = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE);
        switch (sortType){
            case Popular:
                builder.appendPath(PATH_POPULAR);
                break;
            case TopRated:
                builder.appendPath(PATH_TOP_RATED);
                break;
        }

        return tryBuildUrl(builder);
    }

    @Nullable
    private static URL tryBuildUrl(Uri.Builder builder) {
        builder.appendQueryParameter(PARAM_KEY, TMDB_API_KEY);
        URL url = null;
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Film> parseFilmsListJson(String jsonString){

        ArrayList<Film> movies;

        try {
            JSONObject object = new JSONObject(jsonString);

            JSONArray jsonArray = object.getJSONArray("results");
            movies = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++){
                Film film = new Film();

                JSONObject currentJsonObject = jsonArray.getJSONObject(i);

                film.setPosterPath(currentJsonObject.getString("poster_path"));
                film.setTitle(currentJsonObject.getString("title"));
                film.setOverview(currentJsonObject.getString("overview"));
                film.setReleaseDate(currentJsonObject.getString("release_date"));
                film.setVoteAverage(currentJsonObject.getDouble("vote_average"));
                film.setId(currentJsonObject.getInt("id"));

                movies.add(film);
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }

    }

    public static List<Review> parseReviewsListJson(String jsonString){

        ArrayList<Review> reviews;

        try {
            JSONObject object = new JSONObject(jsonString);

            JSONArray jsonArray = object.getJSONArray("results");
            reviews = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++){
                Review review = new Review();

                JSONObject currentJsonObject = jsonArray.getJSONObject(i);

                review.setId(currentJsonObject.getString("id"));
                review.setAuthor(currentJsonObject.getString("author"));
                review.setContent(currentJsonObject.getString("content"));
                review.setUrl(currentJsonObject.getString("url"));

                reviews.add(review);
            }

            return reviews;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }

    }

    public static Uri getPosterUri(Film film){
        return Uri.parse(TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(PATH_DEFAULT_WIDTH)
                .appendPath(film.getPosterPath().replace("/", ""))
                .build();
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
