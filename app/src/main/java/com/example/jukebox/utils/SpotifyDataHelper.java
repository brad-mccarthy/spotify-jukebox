package com.example.jukebox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.jukebox.model.UserProfileDTO;
import com.example.jukebox.service.SpotifyServiceClient;
import com.spotify.android.appremote.api.ConnectionParams;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotifyDataHelper {

    public static final String CLIENT_ID = "ffec034762ab4784beb3f3ade04f6d6c";
    public static final String REDIRECT_URI = "jukebox://callback";
    public static final String CLIENT_SECRET = "3cc2bff89515446caa633a6f3cd7ce63";
    private static final String SPOTIFY_DATA = "spotifyData";
    private static final String USERNAME_KEY = "username";

    public static String getUsername(Context context) {
        SharedPreferences spotifyDataPreferences = context.getSharedPreferences(SPOTIFY_DATA, Context.MODE_PRIVATE);
        if (!usernameExists(context)) {
            SpotifyServiceClient.retrieveUserProfile(context)
                    .enqueue(new Callback<UserProfileDTO>() {
                        @Override
                        public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                            if (response.body() != null) {
                                setUsername(context, response.body().displayName);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                            Log.d("SpotifyDataHelper", "onFailure: failed to get username");
                        }
                    });
        }
        return spotifyDataPreferences.getString(USERNAME_KEY, null);
    }

    private static boolean usernameExists(Context context) {
        SharedPreferences spotifyDataPreferences = context.getSharedPreferences(SPOTIFY_DATA, Context.MODE_PRIVATE);
        return spotifyDataPreferences.getString(USERNAME_KEY, null) != null;
    }

    private static void setUsername(Context context, String newUsername) {
        SharedPreferences spotifyDataPreferences = context.getSharedPreferences(SPOTIFY_DATA, Context.MODE_PRIVATE);
        spotifyDataPreferences.edit().putString(USERNAME_KEY, newUsername).apply();
    }

    public static ConnectionParams getConnectionParams() {
        return new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
    }
}
