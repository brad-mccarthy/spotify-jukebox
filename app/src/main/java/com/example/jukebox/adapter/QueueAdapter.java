package com.example.jukebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.model.song.Song;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class QueueAdapter extends FirestoreRecyclerAdapter<Song, QueueAdapter.QueueViewHolder> {

    public QueueAdapter(FirestoreRecyclerOptions<Song> options) {
        super(options);
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.queue_row, parent, false);
        return new QueueViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull QueueViewHolder holder, int position, @NonNull Song song) {
        holder.bind(song);
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
//            this.albumName = v.findViewById(R.id.queue_album_name);
            this.artistNames = v.findViewById(R.id.queue_artist_names);

            songName.setSelected(true);
            username.setSelected(true);
//            albumName.setSelected(true);
            artistNames.setSelected(true);
        }

        void bind(Song song) {
            songName.setText(song.songName);
            username.setText(song.userName);
//            albumName.setText(song.albumName);
            artistNames.setText(song.artistNames);
            uri = song.uri;
        }
    }
}
