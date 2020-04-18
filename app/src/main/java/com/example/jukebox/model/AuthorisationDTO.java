package com.example.jukebox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AuthorisationDTO {

    @Expose
    @SerializedName("code")
    public String code;

    @Expose
    @SerializedName("state")
    public String state;

    @Expose
    @SerializedName("state")
    public String error;
}
