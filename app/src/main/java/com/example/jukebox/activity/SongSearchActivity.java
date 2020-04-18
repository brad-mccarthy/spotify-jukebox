package com.example.jukebox.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.SearchResultsAdapter;
import com.example.jukebox.model.SearchResultsDTO;
import com.example.jukebox.model.song.Tracks;
import com.example.jukebox.service.SpotifySongServiceClient;
import com.example.jukebox.utils.FirebasePartyHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongSearchActivity extends AppCompatActivity {

    public static final String TAG = "Searching for songs:";
    private RecyclerView recyclerView;
    private SearchResultsAdapter searchAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button searchButton;
    private EditText searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_search);

        searchQuery = findViewById(R.id.songSearchbar);
        searchButton = findViewById(R.id.search_btn);

        setupRecyclerView(getIntent().getStringExtra("partyName"));

        searchButton.setOnClickListener(v -> {
            searchAdapter.clearSeachResults();
            SpotifySongServiceClient.searchForSongs(SongSearchActivity.this, searchQuery.getText().toString())
                    .enqueue(songSearchCallback());
        });
    }

    private Callback<SearchResultsDTO> songSearchCallback() {
        return new Callback<SearchResultsDTO>() {
            @Override
            public void onResponse(Call<SearchResultsDTO> call, Response<SearchResultsDTO> response) {
                Log.d(TAG, "onResponse: success!!");
                if (response.body() != null) {
                    Tracks tracks = response.body().tracks;
                    searchAdapter.addAll(tracks.items);
                }
            }
            @Override
            public void onFailure(Call<SearchResultsDTO> call, Throwable t) {
                Log.d(TAG, "onResponse: fail!! " + t);
            }
        };
    }

    private void setupRecyclerView(String partyName) {
        recyclerView = findViewById(R.id.searchResults);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchAdapter = new SearchResultsAdapter(song -> FirebasePartyHelper.addSongToPartyQueue(this, song, partyName));
        recyclerView.setAdapter(searchAdapter);
    }


}
