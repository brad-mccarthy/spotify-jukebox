package com.example.jukebox.player;

import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.jukebox.model.song.Song;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;

import java.util.List;

import static com.example.jukebox.activity.PlayerActivity.THREE_SECONDS;
import static com.example.jukebox.player.PlayerNotification.buildPausableNotification;
import static com.example.jukebox.player.PlayerNotification.buildPlayableNotification;
import static com.example.jukebox.restservice.SpotifyServiceClient.TAG;
import static com.example.jukebox.utils.FirebasePartyHelper.QUEUE_POSITION_FIELD;
import static com.example.jukebox.utils.FirebasePartyHelper.getReferenceToParty;
import static com.example.jukebox.utils.SpotifyDataHelper.getConnectionParams;

public class JukeboxSessionCallback extends MediaSessionCompat.Callback {

    private static int NOTIFICATION_ID = 12345666;

    private SpotifyAppRemote spotifyAppRemote;
    private int queuePosition;
    private String partyName;
    private MediaPlaybackService mediaPlaybackService;
    private List<Song> queue;
    private MediaSessionCompat mediaSession;


    JukeboxSessionCallback(String partyName,
                           MediaPlaybackService mediaPlaybackService,
                           MediaSessionCompat mediaSession) {
        this.partyName = partyName;
        this.mediaPlaybackService = mediaPlaybackService;
        this.mediaSession = mediaSession;

        FirebasePartyHelper.getAllSongsForAParty(partyName,
                queryDocumentSnapshots -> queue = queryDocumentSnapshots.toObjects(Song.class));
        FirebasePartyHelper.getParty(partyName,
                queryDocumentSnapshots -> queuePosition = (int) (long) queryDocumentSnapshots.get(QUEUE_POSITION_FIELD));

        connectToSpotifyAppRemote();
    }

    private void connectToSpotifyAppRemote() {
        SpotifyAppRemote.connect(mediaPlaybackService, getConnectionParams(),
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d("sessioncallback", "Connected! Yay!");
                        JukeboxSessionCallback.this.spotifyAppRemote = spotifyAppRemote;
                        subscribeToPlayerState();
                    }

                    public void onFailure(Throwable error) {
                        Log.e("sessioncallback error", error.getMessage(), error);
                    }
                });
    }

    @Override
    public void onPlay() {
        super.onPlay();
        mediaPlaybackService.startService(new Intent(mediaPlaybackService, MediaPlaybackService.class));
        mediaSession.setActive(true);
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        mediaPlaybackService.startForeground(NOTIFICATION_ID,
                buildPausableNotification(mediaSession, mediaPlaybackService, getCurrentSongName(), getCurrentSongArtists()));
        play();
    }

    private String getCurrentSongArtists() {
        return queue.get(queuePosition).artistNames;
    }

    private String getCurrentSongName() {
        return queue.get(queuePosition).songName;
    }

    @Override
    public void onPause() {
        super.onPause();
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        mediaPlaybackService.startForeground(NOTIFICATION_ID,
                buildPlayableNotification(mediaSession, mediaPlaybackService, getCurrentSongName(), getCurrentSongArtists()));
        mediaPlaybackService.stopForeground(false);
        pause();
    }

    @Override
    public void onSkipToNext() {
        super.onSkipToNext();
        next();

    }

    @Override
    public void onSkipToPrevious() {
        super.onSkipToPrevious();
        previous();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlaybackService.stopSelf();
        mediaSession.setActive(true);
        mediaPlaybackService.stopForeground(false);
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }

    private void previous() {
        if (isAtStartOfQueue()) {
            restartCurrentSong();
        }
        PlayerApi playerApi = spotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackPosition > THREE_SECONDS) {
                restartCurrentSong();
            } else {
                Song previousSong = queue.get(queuePosition - 1);
                playerApi.play(previousSong.uri);
                queuePosition--;
                updateQueuePositionForFirestore(queuePosition);
            }
        });
    }

    private boolean isAtStartOfQueue() {
        return queuePosition == 0;
    }

    private void restartCurrentSong() {
        if (spotifyAppRemote != null && spotifyAppRemote.isConnected()) {
            spotifyAppRemote.getPlayerApi().seekTo(0);
        }
    }

    private void next() {
        if (spotifyAppRemote == null) {
            Log.d("help", "app remote is null onNext: " + queue.get(queuePosition).uri);

            return;
        }

        if (isAtEndOfQueue()) {
            return;
        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song tro play: " + queue.get(queuePosition + 1).uri);
            spotifyAppRemote.getPlayerApi()
                    .play(queue.get(queuePosition + 1).uri);
            queuePosition++;
            updateQueuePositionForFirestore(queuePosition);

        }
    }

    private boolean isAtEndOfQueue() {
        return queuePosition + 1 >= queue.size();
    }

    private void pause() {
        if (spotifyAppRemote == null) {
            Log.d("help", "app remote is null onPause: " + queue.get(queuePosition).uri);

            return;
        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song to pause: " + queue.get(queuePosition).uri);
            spotifyAppRemote.getPlayerApi().pause();
        }
    }

    private void play() {
        if (spotifyAppRemote == null) {
            Log.d("help", "app remote is null: " + queue.get(queuePosition).uri);
            return;
        }

        if (spotifyAppRemote.isConnected()) {
            Log.d("help", "song tro play: " + queue.get(queuePosition).uri);
            resumeCurrentSongIfPausedOrPlayCurrentSongInQueue();
        }
    }

    private void resumeCurrentSongIfPausedOrPlayCurrentSongInQueue() {
        PlayerApi playerApi = spotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.isPaused && currentSongIsOnPlayer(playerState)) {
                playerApi.resume();
            } else if (!currentSongIsOnPlayer(playerState)) {
                playerApi.play(queue.get(queuePosition).uri);

            }
        });
    }

    private void subscribeToPlayerState() {
        spotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            if (!currentSongIsOnPlayer(playerState)) {
                mediaPlaybackService.startForeground(NOTIFICATION_ID,
                        buildPlayableNotification(mediaSession, mediaPlaybackService, getCurrentSongName(), getCurrentSongArtists()));
            }
            if (currentSongIsFinished(playerState)) {
                next();
            } else if (playerState.isPaused &&
                    mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                onPause();
            } else if (!playerState.isPaused &&
                    mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
                onPlay();
            }
        });
    }

    private void updateQueuePositionForFirestore(Integer position) {
        getReferenceToParty(partyName)
                .update("queuePosition", position)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private boolean currentSongIsFinished(PlayerState playerState) {
        return playerState.isPaused && playerState.playbackPosition == 0;
    }

    private boolean currentSongIsOnPlayer(PlayerState playerState) {
        return queue.get(queuePosition).uri.equalsIgnoreCase(playerState.track.uri);
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                    PlaybackStateCompat.ACTION_PAUSE |
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                    PlaybackStateCompat.ACTION_PLAY |
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackstateBuilder.build());
    }
}