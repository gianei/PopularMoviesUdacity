package com.glsebastiany.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.glsebastiany.popularmovies.model.Film;
import com.glsebastiany.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.FilmViewHolder> {

    private List<Film> films;
    private final FilmClickListener filmClickListener;

    interface FilmClickListener {
        void onFilmClick(Film film);
    }

    public FilmsAdapter(FilmClickListener filmClickListener){
        this.filmClickListener = filmClickListener;
    }

    @Override
    public FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_film, parent, false);

        return new FilmViewHolder(view);

    }

    @Override
    public void onBindViewHolder(FilmViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (films == null){
            return 0;
        }

        return films.size();
    }

    public void setFilms(List<Film> films){
        this.films = films;
        notifyDataSetChanged();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView poster;
        private final View itemView;

        public FilmViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            itemView.setOnClickListener(this);

            this.poster = (ImageView) itemView.findViewById(R.id.iv_film_poster);
        }


        public void bind(){
            Picasso
                    .with(itemView.getContext())
                    .load(NetworkUtils.getPosterUri(films.get(getAdapterPosition())))
                    .into(poster);
        }

        @Override
        public void onClick(View v) {
            filmClickListener.onFilmClick(films.get(getAdapterPosition()));
        }
    }
}
