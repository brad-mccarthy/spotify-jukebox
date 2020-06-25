package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jukebox.R;
import com.example.jukebox.model.song.Song;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

import java.util.List;

import static com.example.jukebox.utils.SpotifyDataHelper.getConnectionParams;

public class PlayerActivity extends AppCompatActivity {

    private Button playButton;
    private SpotifyAppRemote spotifyAppRemote;
    private List<Song> queue;
    private String partyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        partyName = getIntent().getStringExtra("partyName");
        playButton = findViewById(R.id.play_btn);

        FirebasePartyHelper.getAllSongsForAParty(partyName, queryDocumentSnapshots -> queue = queryDocumentSnapshots.toObjects(Song.class)
        );

        playButton.setOnClickListener(click -> {
            if (spotifyAppRemote == null) {
                Toast.makeText(this, "One sec...", Toast.LENGTH_LONG).show();
                return;
            }

            if (spotifyAppRemote.isConnected()) {
                Log.d("help", "song tro play: " + queue.get(0).uri);
                spotifyAppRemote.getPlayerApi()
                        .play(queue.get(0).uri);
            }
        });

        connectToSpotifyAppRemote();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        connectToSpotifyAppRemote();
    }

    private void connectToSpotifyAppRemote() {
        Log.d("sf", "connectToSpotifyAppRemote: sdvdfs");
        SpotifyAppRemote.connect(this, getConnectionParams(),
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d("PlayerActivity", "Connected! Yay!");
                        PlayerActivity.this.spotifyAppRemote = spotifyAppRemote;
                    }

                    public void onFailure(Throwable error) {
                        Log.e("MyActivity", error.getMessage(), error);
                        if (error instanceof NotLoggedInException || error instanceof UserNotAuthorizedException) {
                            startActivity(new Intent(PlayerActivity.this, LoginActivity.class));
                        }
                    }
                });
    }
}
