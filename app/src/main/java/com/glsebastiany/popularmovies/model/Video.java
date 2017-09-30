package com.glsebastiany.popularmovies.model;

/**
 * Created by gianei on 30/09/2017.
 */


import android.content.Context;
import android.widget.Toast;

import com.glsebastiany.popularmovies.R;
import com.glsebastiany.popularmovies.util.IntentActionsHelper;

/**
 * @see <a href="https://developers.themoviedb.org/3/movies/get-movie-videos">MVDB model</href>
 */

public class Video {

    private String id;
    private String site;
    private String name;
    private String key;

    public Video(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void open(Context context){
        if (getSite().trim().equalsIgnoreCase("youtube")){
            IntentActionsHelper.watchYoutubeVideo(context, getKey());
        } else {
            Toast.makeText(context, R.string.error_not_you_tube, Toast.LENGTH_SHORT).show();
        }
    }
}
