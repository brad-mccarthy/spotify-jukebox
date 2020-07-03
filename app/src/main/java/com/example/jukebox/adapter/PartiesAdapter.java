package com.example.jukebox.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.activity.PlayerActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PartiesAdapter extends FirestoreRecyclerAdapter<String, PartiesAdapter.PartiesViewHolder> {

    private Context context;

    public PartiesAdapter(@NonNull FirestoreRecyclerOptions<String> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public PartiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.party_row, parent, false);
        return new PartiesViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull PartiesViewHolder holder, int position, @NonNull String partyName) {
        holder.partyName.setText(partyName);
        holder.joinParty.setOnClickListener(joinPartyOnCLickListener(partyName));
    }

    private View.OnClickListener joinPartyOnCLickListener(String partyName) {
        return onClick -> {
            updateCurrentPartyName(partyName);
            startPlayerActivity(partyName);
        };
    }

    private void updateCurrentPartyName(String partyName) {
        SharedPreferences partyNamePreferences = context.getSharedPreferences("partyName", Context.MODE_PRIVATE);
        partyNamePreferences.edit().putString("currentPartyName", partyName).apply();
    }

    private void startPlayerActivity(String partyName) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.putExtra("partyName", partyName);
        playerActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(playerActivityIntent);
    }

    static class PartiesViewHolder extends RecyclerView.ViewHolder {
        TextView partyName;
        Button joinParty;

        PartiesViewHolder(View v) {
            super(v);
            this.partyName = v.findViewById(R.id.partyName);
            this.joinParty = v.findViewById(R.id.joinParty);
        }
    }
}
