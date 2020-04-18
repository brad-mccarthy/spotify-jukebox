package com.example.jukebox.service;

import android.content.Context;

import com.example.jukebox.model.SearchResultsDTO;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.jukebox.service.SpotifyServiceClient.getAccessToken;

public class SpotifySongServiceClient {

    public static Call<SearchResultsDTO> searchForSongs(Context context,
                                                        String query) {
        String baseUri = "https://api.spotify.com/v1/";

        SpotifySongService spotifySongService = setupSpotifySongService(baseUri);

        return spotifySongService.search("Bearer " + getAccessToken(context), query, "track");
    }

    private static SpotifySongService setupSpotifySongService(String baseUri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUri)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(SpotifySongService.class);
    }
}
