package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.QueueAdapter;
import com.example.jukebox.model.song.Song;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

import java.util.List;

import static com.example.jukebox.utils.SpotifyDataHelper.getConnectionParams;

public class QueueActivity extends AppCompatActivity {

    private String partyName;
    private QueueAdapter queueAdapter;
    private SpotifyAppRemote spotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        partyName = getIntent().getStringExtra("partyName");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startSongSearchActivity();
        });

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(view -> {
            if (spotifyAppRemote.isConnected()) {
                Log.d("help", "onCreate: " + queueAdapter.getTopOfQueue().uri);
                spotifyAppRemote.getPlayerApi()
                        .play(queueAdapter.getTopOfQueue().uri);
            }
        });
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        connectToSpotifyAppRemote();
    }

    private void connectToSpotifyAppRemote() {
        SpotifyAppRemote.connect(this, getConnectionParams(),
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d("MainActivity", "Connected! Yay!");
                        QueueActivity.this.spotifyAppRemote = spotifyAppRemote;
                    }

                    public void onFailure(Throwable error) {
                        Log.e("MyActivity", error.getMessage(), error);
                        if (error instanceof NotLoggedInException || error instanceof UserNotAuthorizedException) {
                            startActivity(new Intent(QueueActivity.this, LoginActivity.class));
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queueAdapter.clearQueue();
        FirebasePartyHelper.getAllSongsForAParty(partyName, queryDocumentSnapshots -> {
            List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
            queueAdapter.addAll(songs);
            queueAdapter.notifyDataSetChanged();
        });
    }

    private void startSongSearchActivity() {
        Intent intent = new Intent(QueueActivity.this, SongSearchActivity.class);
        intent.putExtra("partyName", partyName);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.songQueue);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        queueAdapter = new QueueAdapter();
        recyclerView.setAdapter(queueAdapter);
    }

}
