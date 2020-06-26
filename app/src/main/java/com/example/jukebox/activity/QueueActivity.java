package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.QueueAdapter;
import com.example.jukebox.model.song.Song;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class QueueActivity extends AppCompatActivity {

    private String partyName;
    private QueueAdapter queueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        partyName = getIntent().getStringExtra("partyName");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startSongSearchActivity());

        Button playButton = findViewById(R.id.play_button);
        setupRecyclerView();
    }

    private void startSongSearchActivity() {
        Intent intent = new Intent(QueueActivity.this, SongSearchActivity.class);
        intent.putExtra("partyName", partyName);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        Query query = FirebasePartyHelper.getReferenceToSongs(partyName);
        FirestoreRecyclerOptions<Song> options = new FirestoreRecyclerOptions.Builder<Song>()
                .setQuery(query, Song.class)
                .setLifecycleOwner(this)
                .build();
        RecyclerView recyclerView = findViewById(R.id.songQueue);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        queueAdapter = new QueueAdapter(options);
        recyclerView.setAdapter(queueAdapter);
    }

}
