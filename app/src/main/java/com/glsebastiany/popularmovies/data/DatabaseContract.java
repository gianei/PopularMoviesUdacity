package com.glsebastiany.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;


public class DatabaseContract {

    public static final String AUTHORITY = "com.glsebastiany.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_FILM_ID = "film_id";


        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE "  + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_FILM_ID + " INTEGER NOT NULL " +
                        ");";
        }

        @NonNull
        public static String getDropTableSql() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}
