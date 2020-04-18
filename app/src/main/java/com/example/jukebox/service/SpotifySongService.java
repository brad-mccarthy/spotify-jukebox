package com.example.jukebox.service;

import com.example.jukebox.model.SearchResultsDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SpotifySongService {

    @GET("search")
    Call<SearchResultsDTO> search(@Header("Authorization") String accessToken,
                                  @Query("q") String queryKeywords,
                                  @Query("type") String types);
}
