package com.example.jukebox.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jukebox.R;
import com.example.jukebox.player.MediaPlaybackService;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.example.jukebox.utils.SpotifyDataHelper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.jukebox.utils.FirebasePartyHelper.HOST_FIELD;

public class PlayerActivity extends AppCompatActivity {

    public static final int THREE_SECONDS = 3000;
    private boolean userIsHost;
    private Button playButton;
    private Button pauseButton;
    private Button nextButton;
    private Button queueButton;
    private Button previousButton;
    private String partyName;
    private MediaBrowserCompat mediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {

                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    // Create a MediaControllerCompat
                    try {
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(PlayerActivity.this, // Context
                                        token);

                        // Save the controller
                        MediaControllerCompat.setMediaController(PlayerActivity.this, mediaController);

                        buildTransportControls();

                    } catch (RemoteException e) {
                        Log.d("mmmm", "onConnected: remote exception xxx");
                    }
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                    super.onConnectionFailed();
                    Log.d("llll", "onConnectionFailed: badddd");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        partyName = getIntent().getStringExtra("partyName");

        setupMediaBrowserIfUserIsPartyHost();
    }

    private void setupMediaBrowserIfUserIsPartyHost() {
        FirebasePartyHelper.getParty(partyName,
                queryDocumentSnapshots -> {
                    String hostUserName = (String) queryDocumentSnapshots.get(HOST_FIELD);
                    String currentUsername = SpotifyDataHelper.getCurrentUsername(this);
                    if (hostUserName != null && hostUserName.equals(currentUsername)) {
                        userIsHost = true;
                        setupMediaBrowser();
                    } else {
                        userIsHost = false;
                    }
                });
    }

    private void setupMediaBrowser() {
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlaybackService.class),
                connectionCallbacks,
                null);
        mediaBrowser.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userIsHost) {
            mediaBrowser.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
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

    private void startQueueActivity() {
        Intent queueActivityIntent = new Intent(this, QueueActivity.class);
        queueActivityIntent.putExtra("partyName", partyName);
        queueActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(queueActivityIntent);
    }

    private void previous() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);
        mediaController.getTransportControls().skipToPrevious();
    }

    private void next() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);
        mediaController.getTransportControls().skipToNext();
    }

    private void pause() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);
        mediaController.getTransportControls().pause();
    }

    private void play() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);
        mediaController.getTransportControls().play();
    }

    void buildTransportControls() {
        findButtonViews();
        enableButtons();
        setPlayerOnClickListeners();

        // Display the initial state
//        MediaMetadataCompat metadata = mediaController.getMetadata();
//        PlaybackStateCompat pbState = mediaController.getPlaybackState();
//
//        // Register a Callback to stay in sync
//        mediaController.registerCallback(controllerCallback);
    }

    private void enableButtons() {
        playButton.setEnabled(true);
        pauseButton.setEnabled(true);
        nextButton.setEnabled(true);
        previousButton.setEnabled(true);
    }
}
