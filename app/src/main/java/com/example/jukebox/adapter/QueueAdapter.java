package com.example.jukebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.model.song.Song;

import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {

    private List<Song> queue;

    public QueueAdapter() {
        this.queue = new ArrayList<>();
    }

    public void addAll(List<Song> songs) {
        queue.addAll(songs);
        notifyDataSetChanged();
    }

    public void clearQueue() {
        queue.clear();
        notifyDataSetChanged();
    }

    public Song getTopOfQueue() {
        return queue.get(0);
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.queue_row, parent, false);
        return new QueueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QueueViewHolder holder, int position) {
        holder.bind(queue.get(position));
    }

    @Override
    public int getItemCount() {
        return queue.size();
    }

    static class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView username;
        TextView albumName;
        TextView artistNames;
        String uri;

        QueueViewHolder(View v) {
            super(v);
            this.songName = v.findViewById(R.id.queue_song_name);
            this.username = v.findViewById(R.id.queue_username);
            this.albumName = v.findViewById(R.id.queue_album_name);
            this.artistNames = v.findViewById(R.id.queue_artist_names);
        }

        void bind(Song song) {
            songName.setText(song.songName);
            username.setText(song.userName);
            albumName.setText(song.albumName);
            artistNames.setText(song.artistNames);
            uri = song.uri;
        }
    }
}
