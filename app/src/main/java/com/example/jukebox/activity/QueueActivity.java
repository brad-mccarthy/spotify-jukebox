package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.QueueAdapter;
import com.example.jukebox.model.song.Song;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

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


    @Override
    protected void onResume() {
        super.onResume();
        queueAdapter.clearQueue();
        FirebasePartyHelper.test(partyName).addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (e != null || queryDocumentSnapshots == null) {
                Log.w("HERE21", "Listen failed.", e);
                return;
            }

            List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
            queueAdapter.addAll(songs);
            queueAdapter.notifyDataSetChanged();
        });
//        FirebasePartyHelper.getAllSongsForAParty(partyName, queryDocumentSnapshots -> {
//            List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
//            queueAdapter.addAll(songs);
//            queueAdapter.notifyDataSetChanged();
//        });
    }

    private void startSongSearchActivity() {
        Intent intent = new Intent(QueueActivity.this, SongSearchActivity.class);
        intent.putExtra("partyName", partyName);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.songQueue);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        queueAdapter = new QueueAdapter();
        recyclerView.setAdapter(queueAdapter);
    }

}
