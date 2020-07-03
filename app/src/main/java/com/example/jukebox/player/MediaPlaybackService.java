package com.example.jukebox.player;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.jukebox.R;

import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        MediaSessionCompat mediaSession = new MediaSessionCompat(this, "jukebox_media_browser_service");

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(new JukeboxSessionCallback(getCurrentPartyName(), this, mediaSession));

        // Set the session's token so that client activities can communicate with it.
        MediaSessionCompat.Token sessionToken = mediaSession.getSessionToken();
        setSessionToken(sessionToken);
    }

    private String getCurrentPartyName() {
        SharedPreferences partyNamePreferences = getSharedPreferences("partyName", MODE_PRIVATE);
        return partyNamePreferences.getString("currentPartyName", "");
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }
}