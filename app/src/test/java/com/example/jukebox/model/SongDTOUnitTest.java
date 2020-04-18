package com.example.jukebox.model;

import com.example.jukebox.model.song.Artist;
import com.example.jukebox.model.song.SongDTO;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SongDTOUnitTest {

    private SongDTO testInstance;

    @Before
    public void setup() {
        testInstance = new SongDTO();
    }

    @Test
    public void shouldJoinArtistsSuccessfully() {
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("The Beatles"));
        artists.add(new Artist("Led Zeppelin"));

        testInstance.artists = artists;

        String joined = testInstance.joinArtists();

        assertThat(joined, is("The Beatles, Led Zeppelin"));
    }
}