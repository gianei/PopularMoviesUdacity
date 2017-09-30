package com.glsebastiany.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glsebastiany.popularmovies.model.Video;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.FilmViewHolder> {

    private List<Video> videos;
    private final VideoClickListener videoClickListener;

    interface VideoClickListener {
        void onFilmClick(Video video);
    }

    public VideosAdapter(VideoClickListener videoClickListener){
        this.videoClickListener = videoClickListener;
    }

    @Override
    public FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);

        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilmViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (videos == null){
            return 0;
        }

        return videos.size();
    }

    public void setVideos(List<Video> videos){
        this.videos = videos;
        notifyDataSetChanged();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView videoText;
        private final View itemView;

        public FilmViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            itemView.setOnClickListener(this);

            this.videoText = itemView.findViewById(R.id.tv_video);
        }


        public void bind(){
            videoText.setText(videos.get(getAdapterPosition()).getName());
        }

        @Override
        public void onClick(View v) {
            videoClickListener.onFilmClick(videos.get(getAdapterPosition()));
        }
    }
}
