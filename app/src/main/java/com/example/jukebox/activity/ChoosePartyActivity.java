package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.adapter.PartiesAdapter;
import com.example.jukebox.service.SpotifyServiceClient;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        if (intent.getData() != null) {
            retrieveAccessCodeIfItDoesNotExist(intent);
        }

        FloatingActionButton addPartyFab = findViewById(R.id.add_party);
        addPartyFab.setOnClickListener(view -> startAddPartyActivity());
    }

    private void startAddPartyActivity() {
        Intent addPartyIntent = new Intent(ChoosePartyActivity.this, AddPartyActivity.class);
        startActivity(addPartyIntent);
    }


    private void retrieveAccessCodeIfItDoesNotExist(Intent intent) {
        String code = intent.getData().getQueryParameter(SPOTIFY_CODE);
        if (!accessCodeExists(this)) {
            SpotifyServiceClient.retrieveTokens(this, code);
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.partiesRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        partiesAdapter = new PartiesAdapter(getApplicationContext());
        recyclerView.setAdapter(partiesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebasePartyHelper.getAllParties(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                addPartiesToAdapter(task);
            } else {
                Log.w(TAG, "Error getting parties.", task.getException());
            }
        });
    }

    private void addPartiesToAdapter(Task<QuerySnapshot> task) {
        partiesAdapter.clearParties();
        Log.d(TAG, "addPartiesToAdapter: herree");
        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, "addPartiesToAdapter: " + document.getId());
            partiesAdapter.addParty(document.getId());
        }
    }
}
