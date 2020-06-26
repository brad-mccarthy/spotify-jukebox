package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.PartiesAdapter;
import com.example.jukebox.service.SpotifyServiceClient;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.time.Clock;

import static com.example.jukebox.utils.TokenSharedPreferencesHelper.accessCodeExists;

public class ChoosePartyActivity extends AppCompatActivity {

    public static final String SPOTIFY_CODE = "code";
    public static Clock clock = Clock.systemDefaultZone();
    private RecyclerView recyclerView;
    private PartiesAdapter partiesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String TAG = "ChoosePartyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_party);

        setupRecyclerView();

        Intent intent = getIntent();

        retrieveAccessCodeIfItDoesNotExist(intent);

        FloatingActionButton addPartyFab = findViewById(R.id.add_party);
        addPartyFab.setOnClickListener(view -> startAddPartyActivity());
    }

    @Override
    protected void onStart() {
        super.onStart();
        partiesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        partiesAdapter.stopListening();
    }

    private void startAddPartyActivity() {
        Intent addPartyIntent = new Intent(ChoosePartyActivity.this, AddPartyActivity.class);
        startActivity(addPartyIntent);
    }


    private void retrieveAccessCodeIfItDoesNotExist(Intent intent) {
        if (intent.getData() == null) {
            return;
        }

        String code = intent.getData().getQueryParameter(SPOTIFY_CODE);
        if (!accessCodeExists(this)) {
            SpotifyServiceClient.retrieveTokens(this, code);
        }
    }

    private void setupRecyclerView() {
        Query query = FirebasePartyHelper.getReferenceToParties()
                .limit(50);
        FirestoreRecyclerOptions<String> options = new FirestoreRecyclerOptions.Builder<String>()
                .setQuery(query, DocumentSnapshot::getId)
                .build();

        recyclerView = findViewById(R.id.partiesRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        partiesAdapter = new PartiesAdapter(options, this);
        recyclerView.setAdapter(partiesAdapter);
    }
}
