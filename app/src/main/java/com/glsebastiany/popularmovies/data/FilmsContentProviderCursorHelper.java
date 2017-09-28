package com.glsebastiany.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.glsebastiany.popularmovies.model.Film;

/**
 * Created by gianei on 27/09/2017.
 */

public class FilmsContentProviderCursorHelper {

    public static void addFavoriteMovie(Context context, Film film) {
        ContentValues filmContentValue = new ContentValues();
        filmContentValue.put(DatabaseContract.FavoriteEntry._ID, film.getId());
        filmContentValue.put(DatabaseContract.FavoriteEntry.COLUMN_TITLE, film.getTitle());

        context.getContentResolver().insert(DatabaseContract.FavoriteEntry.CONTENT_URI, filmContentValue);
    }

    public static void removeFavoriteMovie(Context context, String id) {
        Uri deleteUri = DatabaseContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(id).build();

        context.getContentResolver().delete(deleteUri, null, null);
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
}
