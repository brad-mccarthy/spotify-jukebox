package com.example.jukebox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefreshedTokenDTO {

    @Expose
    @SerializedName("access_token")
    public String accessToken;

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
