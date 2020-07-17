package com.example.jukebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jukebox.R;
import com.example.jukebox.model.song.SongDTO;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    private final OnSearchResultClickListener onSearchResultClickListener;
    private List<SongDTO> searchResults;

    public SearchResultsAdapter(OnSearchResultClickListener onSearchResultClickListener) {
        this.onSearchResultClickListener = onSearchResultClickListener;
        searchResults = new ArrayList<>();
    }

    @NonNull
    @Override
    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_row, parent, false);
        return new SearchResultsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsViewHolder holder, int position) {
        holder.bind(searchResults.get(position), onSearchResultClickListener);
    }

    public void addAll(List<SongDTO> songs) {
        searchResults.addAll(songs);
        notifyDataSetChanged();
    }

    public void clearSeachResults() {
        searchResults.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    static class SearchResultsViewHolder extends RecyclerView.ViewHolder {

        TextView songName;
        TextView albumName;
        TextView artistName;

        SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.song_artist);

            songName.setSelected(true);
            artistName.setSelected(true);
        }

        void bind(SongDTO song, OnSearchResultClickListener listener) {
            songName.setText(song.name);
            artistName.setText(song.joinArtists());
            itemView.setOnClickListener(v -> listener.onSearchResultClick(song));
        }
    }
}
