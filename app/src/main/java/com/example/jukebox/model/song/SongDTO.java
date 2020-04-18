package com.example.jukebox.model.song;

import java.util.List;
import java.util.stream.Collectors;

public class SongDTO {

    public String name;

    public List<Artist> artists;

    public Album album;

    public String uri;

    public String joinArtists() {
        return artists.stream()
                .map(artist -> artist.name)
                .collect(Collectors.joining(", "));
    }
}
