package com.glsebastiany.popularmovies.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created by gianei on 30/09/2017.
 */

public abstract class CachedAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    private static final String TAG = CachedAsyncTaskLoader.class.getSimpleName();

    private T mTaskData = null;

    public CachedAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mTaskData != null) {
            deliverResult(mTaskData);
        } else {
            forceLoad();
        }
    }

    @Override
    public T loadInBackground() {
        try {

            return internalLoadInBackground();

        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    public abstract T internalLoadInBackground() throws Exception;

    public void deliverResult(T data) {
        mTaskData = data;
        super.deliverResult(data);
    }
}
