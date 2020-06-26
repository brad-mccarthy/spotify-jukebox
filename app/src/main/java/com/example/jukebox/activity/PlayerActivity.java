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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.jukebox.utils.SpotifyDataHelper.getConnectionParams;

public class PlayerActivity extends AppCompatActivity {

    private Button playButton;
    private Button pauseButton;
    private Button nextButton;
    private SpotifyAppRemote spotifyAppRemote;
    private List<Song> queue;
    private String partyName;
    private Button queueButton;
    private Integer currentSongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        partyName = getIntent().getStringExtra("partyName");
        playButton = findViewById(R.id.play_btn);
        pauseButton = findViewById(R.id.pause_btn);
        nextButton = findViewById(R.id.next_btn);
        queueButton = findViewById(R.id.queue_btn);
        currentSongIndex = 0;

        FirebasePartyHelper.getAllSongsForAParty(partyName,
                queryDocumentSnapshots -> queue = queryDocumentSnapshots.toObjects(Song.class));

        playButton.setOnClickListener(click -> play());
        pauseButton.setOnClickListener(click -> pause());
        nextButton.setOnClickListener(click -> next());
        queueButton.setOnClickListener(click -> startQueueActivity());

        connectToSpotifyAppRemote();
    }

    private void startQueueActivity() {
        Intent queueActivityIntent = new Intent(this, QueueActivity.class);
        queueActivityIntent.putExtra("partyName", partyName);
        queueActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(queueActivityIntent);
    }

    private void next() {
        if (spotifyAppRemote == null) {
            Toast.makeText(this, "One sec...", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentSongIndex + 1 > queue.size()) {
            Toast.makeText(this, "You're at the end of the queue bozo", Toast.LENGTH_LONG).show();

        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song tro play: " + queue.get(currentSongIndex + 1).uri);
            spotifyAppRemote.getPlayerApi()
                    .play(queue.get(currentSongIndex + 1).uri);
            currentSongIndex += 1;
        }
    }

    private void pause() {
        if (spotifyAppRemote == null) {
            Toast.makeText(this, "One sec...", Toast.LENGTH_LONG).show();
            return;
        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song to pause: " + queue.get(currentSongIndex).uri);
            spotifyAppRemote.getPlayerApi().pause();
        }
    }

    private void play() {
        if (spotifyAppRemote == null) {
            Toast.makeText(this, "One sec...", Toast.LENGTH_LONG).show();
            return;
        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song tro play: " + queue.get(0).uri);
            spotifyAppRemote.getPlayerApi()
                    .play(queue.get(0).uri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        connectToSpotifyAppRemote();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
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
