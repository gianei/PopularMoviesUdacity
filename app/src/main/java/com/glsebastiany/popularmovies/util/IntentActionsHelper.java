package com.glsebastiany.popularmovies.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.glsebastiany.popularmovies.R;

/**
 * Created by gianei on 30/09/2017.
 */

public class IntentActionsHelper {

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            if (webIntent.resolveActivity(context.getPackageManager()) != null){
                context.startActivity(webIntent);
            } else {
                Toast.makeText(context, R.string.you_tube_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
