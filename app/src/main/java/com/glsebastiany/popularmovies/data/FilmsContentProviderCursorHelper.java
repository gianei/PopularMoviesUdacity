package com.glsebastiany.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.glsebastiany.popularmovies.model.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gianei on 27/09/2017.
 */

public class FilmsContentProviderCursorHelper {

    public static void addFavoriteMovie(Context context, Film film) {
        ContentValues filmContentValue = new ContentValues();
        filmContentValue.put(DatabaseContract.FavoriteEntry._ID, film.getId());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_POSTER_PATH, film.getPosterPath());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_TITLE, film.getTitle());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_OVERVIEW, film.getOverview());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_RELEASE_DATE, film.getReleaseDate());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, film.getVoteAverage());

        context.getContentResolver().insert(DatabaseContract.FavoriteEntry.CONTENT_URI, filmContentValue);
    }

    public static void removeFavoriteMovie(Context context, String id) {
        Uri deleteUri = DatabaseContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(id).build();

        context.getContentResolver().delete(deleteUri, null, null);
    }

    public static Cursor getFilmsCursor(Context context) {
        Uri uri = DatabaseContract.FavoriteEntry.CONTENT_URI;
        return context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
    }

    public static Cursor getFilmCursor(Context context, String id) {

        Uri uri = DatabaseContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        return context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
    }

    public static List<Film> getFilmsFromCursor(Cursor cursor){
        if (cursor == null){
            return new ArrayList<>(0);
        }

        ArrayList<Film> films = new ArrayList<>(cursor.getCount());

        int idIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry._ID);
        int posterPathIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_POSTER_PATH);
        int titleIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_TITLE);
        int overviewIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_VOTE_AVERAGE);

        if (cursor.moveToNext()){
            Film film = new Film();

            film.setId(cursor.getInt(idIndex));
            film.setPosterPath(cursor.getString(posterPathIndex));
            film.setTitle(cursor.getString(titleIndex));
            film.setOverview(cursor.getString(overviewIndex));
            film.setReleaseDate(cursor.getString(releaseDateIndex));
            film.setVoteAverage(cursor.getDouble(voteAverageIndex));

            films.add(film);
        }

        return films;
    }
}
