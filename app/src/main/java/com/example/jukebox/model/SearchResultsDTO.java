package com.example.jukebox.model;

import com.example.jukebox.model.song.Tracks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResultsDTO {

    @Expose
    @SerializedName("tracks")
    public Tracks tracks;
}
