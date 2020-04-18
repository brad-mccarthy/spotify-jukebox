package com.example.jukebox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenDTO {

    @Expose
    @SerializedName("access_token")
    public String accessToken;

    @Expose
    @SerializedName("refresh_token")
    public String refreshToken;

    @Expose
    @SerializedName("token_type")
    public String tokenType;

    @Expose
    @SerializedName("scope")
    public String scope;

    @Expose
    @SerializedName("expires_in")
    public Integer expiresIn;

}
