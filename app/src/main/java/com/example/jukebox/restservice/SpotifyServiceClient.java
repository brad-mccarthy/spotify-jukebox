package com.example.jukebox.restservice;

import android.content.Context;
import android.util.Log;

import com.example.jukebox.model.RefreshedTokenDTO;
import com.example.jukebox.model.TokenDTO;
import com.example.jukebox.model.UserProfileDTO;
import com.example.jukebox.utils.TokenSharedPreferencesHelper;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.jukebox.utils.SpotifyDataHelper.CLIENT_ID;
import static com.example.jukebox.utils.SpotifyDataHelper.CLIENT_SECRET;
import static com.example.jukebox.utils.SpotifyDataHelper.REDIRECT_URI;

public class SpotifyServiceClient {

    public static final String TAG = "SpotifyService";

    public static String getAccessToken(Context context) {
        if (TokenSharedPreferencesHelper.isAccessTokenExpired(context)) {
            refreshAccessToken(context);
        }
        return TokenSharedPreferencesHelper.getAccessToken(context);
    }

    public static void retrieveTokens(Context context, String code) {
        String baseUri = "https://accounts.spotify.com/api/";

        SpotifyService spotifyService = setupSpotifyService(baseUri);

        spotifyService.retrieveToken(CLIENT_ID, CLIENT_SECRET, code, "authorization_code", REDIRECT_URI)
                .enqueue(retrievedTokensCallback(context));
    }

    private static Callback<TokenDTO> retrievedTokensCallback(Context context) {
        return new Callback<TokenDTO>() {
            @Override
            public void onResponse(Call<TokenDTO> call, Response<TokenDTO> response) {
                Log.w(TAG, "successfully retrieved tokens");
                if (response.body() != null) {
                    TokenSharedPreferencesHelper.updateAccessToken(context,
                            response.body().accessToken, response.body().refreshToken, response.body().expiresIn);
                } else {
                    Log.w(TAG, "unable to retrieve refresh token");
                }
            }

            @Override
            public void onFailure(Call<TokenDTO> call, Throwable t) {
                Log.w(TAG, "Error retrieving tokens " + t);

            }
        };
    }

    private static void refreshAccessToken(Context context) {
        String baseUri = "https://accounts.spotify.com/api/";

        SpotifyService spotifyService = setupSpotifyService(baseUri);

        byte[] credentials = (CLIENT_ID + ":" + CLIENT_SECRET).getBytes();
        String header = "Basic " + Base64.getEncoder().encodeToString(credentials);

        spotifyService.refreshAccessToken(header, TokenSharedPreferencesHelper.getRefreshToken(context), "refresh_token")
                .enqueue(refreshedTokenCallback(context));
    }

    private static Callback<RefreshedTokenDTO> refreshedTokenCallback(Context context) {
        return new Callback<RefreshedTokenDTO>() {
            @Override
            public void onResponse(Call<RefreshedTokenDTO> call, Response<RefreshedTokenDTO> response) {
                if (response.body() != null) {
                    Log.w(TAG, "got refreshed access token");
                    TokenSharedPreferencesHelper.updateAccessToken(context,
                            response.body().accessToken, response.body().expiresIn);
                } else {
                    Log.w(TAG, "unable to get refresh token");
                }
            }

            @Override
            public void onFailure(Call<RefreshedTokenDTO> call, Throwable t) {
                Log.w(TAG, "unable to get refresh token " + t);

            }
        };
    }

    public static Call<UserProfileDTO> retrieveUserProfile(Context context) {
        String baseUri = "https://api.spotify.com/v1/";

        SpotifyService spotifyService = setupSpotifyService(baseUri);

        return spotifyService.retrieveUsername("Bearer " + getAccessToken(context));
    }

    private static SpotifyService setupSpotifyService(String baseUri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUri)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(SpotifyService.class);
    }
}
