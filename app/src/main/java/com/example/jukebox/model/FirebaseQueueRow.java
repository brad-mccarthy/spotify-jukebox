package com.example.jukebox.model;

import android.content.Context;

import com.example.jukebox.model.song.SongDTO;
import com.example.jukebox.utils.SpotifyDataHelper;
import com.google.firebase.firestore.FieldValue;

import lombok.Getter;

@Getter
public class FirebaseQueueRow {

    private FieldValue timestamp;
    private String songName;
    private String albumName;
    private String artistNames;
    private String userName;
    private String uri;

    public FirebaseQueueRow() {}

    public FirebaseQueueRow(Context context, SongDTO song) {
        timestamp = FieldValue.serverTimestamp();
        songName = song.name;
        albumName = song.album.name;
        artistNames = song.joinArtists();
        userName = SpotifyDataHelper.getUsername(context);
        uri = song.uri;
    }

}
