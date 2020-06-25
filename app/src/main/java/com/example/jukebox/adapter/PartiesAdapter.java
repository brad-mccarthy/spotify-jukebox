package com.example.jukebox.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.activity.PlayerActivity;
import com.example.jukebox.activity.QueueActivity;
import com.example.jukebox.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PartiesAdapter extends RecyclerView.Adapter<PartiesAdapter.PartiesViewHolder> {

    private List<String> parties;
    private Context context;

    public PartiesAdapter(Context context) {
        this.context = context;
        parties = new ArrayList<>();
    }

    @NonNull
    @Override
    public PartiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.party_row, parent, false);
        return new PartiesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PartiesViewHolder holder, int position) {
        String partyName = parties.get((position));
        holder.partyName.setText(partyName);
        holder.joinParty.setOnClickListener(joinPartyOnCLickListener(partyName));
    }

    private View.OnClickListener joinPartyOnCLickListener(String partyName) {
        return onClick -> startPlayerActivity(partyName);
    }

    private void startPlayerActivity(String partyName) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.putExtra("partyName", partyName);
        playerActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(playerActivityIntent);
    }

    @Override
    public int getItemCount() {
        return parties.size();
    }

    public void addParty(String party) {
        parties.add(party);
        notifyDataSetChanged();
    }

    public void clearParties() {
        parties.clear();
        notifyDataSetChanged();
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
