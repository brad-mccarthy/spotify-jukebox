package com.example.jukebox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileDTO {

    @Expose
    @SerializedName("display_name")
    public String displayName;
}
