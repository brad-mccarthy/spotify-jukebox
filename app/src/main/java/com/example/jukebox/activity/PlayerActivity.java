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
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.types.PlayerState;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.jukebox.utils.SpotifyDataHelper.getConnectionParams;

public class PlayerActivity extends AppCompatActivity {

    public static final int THREE_SECONDS = 3000;
    private Button playButton;
    private Button pauseButton;
    private Button nextButton;
    private Button queueButton;
    private Button previousButton;
    private SpotifyAppRemote spotifyAppRemote;
    private List<Song> queue;
    private String partyName;
    private Integer currentSongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        partyName = getIntent().getStringExtra("partyName");
        setupPlayerButtons();
        currentSongIndex = 0;

        FirebasePartyHelper.getAllSongsForAParty(partyName,
                queryDocumentSnapshots -> queue = queryDocumentSnapshots.toObjects(Song.class));

        connectToSpotifyAppRemote();
    }

    private void setupPlayerButtons() {
        findButtonViews();
        setPlayerOnClickListeners();
    }

    private void findButtonViews() {
        playButton = findViewById(R.id.play_btn);
        pauseButton = findViewById(R.id.pause_btn);
        nextButton = findViewById(R.id.next_btn);
        queueButton = findViewById(R.id.queue_btn);
        previousButton = findViewById(R.id.previous_btn);
    }

    private void setPlayerOnClickListeners() {
        playButton.setOnClickListener(click -> play());
        pauseButton.setOnClickListener(click -> pause());
        nextButton.setOnClickListener(click -> next());
        previousButton.setOnClickListener(click -> previous());
        queueButton.setOnClickListener(click -> startQueueActivity());
    }

    private void previous() {
        if (currentSongIndex == 0) {
            restartCurrentSong();
        }

        PlayerApi playerApi = spotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackPosition < THREE_SECONDS) {
                restartCurrentSong();
            } else {
                Song previosSong = queue.get(currentSongIndex - 1);
                playerApi.play(previosSong.uri);
                currentSongIndex--;
            }
        });
    }

    private void restartCurrentSong() {
        if (spotifyAppRemote != null && spotifyAppRemote.isConnected()) {
            spotifyAppRemote.getPlayerApi().seekTo(0);
        }
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
            currentSongIndex++;
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
            Log.d("help", "song tro play: " + queue.get(currentSongIndex).uri);
            resumeCurrentSongIfPausedOrPlayCurrentSongInQueue();
        }
    }

    private void resumeCurrentSongIfPausedOrPlayCurrentSongInQueue() {
        PlayerApi playerApi = spotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.isPaused && currentSongIsOnPlayer(playerState)) {
                playerApi.resume();
            } else {
                playerApi.play(queue.get(currentSongIndex).uri);
            }
        });
    }

    private boolean currentSongIsOnPlayer(PlayerState playerState) {
        return queue.get(currentSongIndex).songName.equals(playerState.track.name);
    }

    public void subscribeToPlayerState() {
        spotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            String currentPlayingTrackName = playerState.track.name;
            Log.d("UMMM", "subscribeToPlayerState: HELPY " + currentPlayingTrackName);
            if (!currentSongIsOnPlayer(playerState) || currentSongIsFinished(playerState)) {
                next();
            }
        });
    }

    private boolean currentSongIsFinished(PlayerState playerState) {
        Log.d("VVVV", "currentSongIsFinished: " + playerState.isPaused + " " + playerState.playbackPosition);
        return playerState.isPaused && playerState.playbackPosition == 0;
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
                        subscribeToPlayerState();
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
