package com.glsebastiany.popularmovies.model;

/**
 * Created by gianei on 30/09/2017.
 */


/**
 * @see <a href="https://developers.themoviedb.org/3/movies/get-movie-reviews">MVDB model</href>
 */

public class Review {

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
