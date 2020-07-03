package com.example.jukebox.restservice;

import com.example.jukebox.model.RefreshedTokenDTO;
import com.example.jukebox.model.TokenDTO;
import com.example.jukebox.model.UserProfileDTO;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SpotifyService {

    @GET("me")
    Call<UserProfileDTO> retrieveUsername(@Header("Authorization") String accessToken);

    @FormUrlEncoded
    @POST("token")
    Call<TokenDTO> retrieveToken(@Field("client_id") String clientId,
                                 @Field("client_secret") String clientSecret,
                                 @Field("code") String code,
                                 @Field("grant_type") String grantType,
                                 @Field("redirect_uri") String redirectUri);

    @FormUrlEncoded
    @POST("token")
    Call<RefreshedTokenDTO> refreshAccessToken(@Header("Authorization") String authorisation,
                                               @Field("refresh_token") String refreshToken,
                                               @Field("grant_type") String grantType);

}
