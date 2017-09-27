package com.glsebastiany.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.glsebastiany.popularmovies.data.DatabaseContract.FavoriteEntry;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popularMovies.db";

    private static final int VERSION = 1;


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(FavoriteEntry.getCreateTableSql());

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(FavoriteEntry.getDropTableSql());
        onCreate(db);

    }

}
