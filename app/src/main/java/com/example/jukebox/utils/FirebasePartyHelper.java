package com.example.jukebox.utils;

import android.content.Context;

import com.example.jukebox.model.FirebaseQueueRow;
import com.example.jukebox.model.song.SongDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebasePartyHelper {

    public static final String PARTY_COLLECTION = "party";
    public static final String SONG_COLLECTION = "songs";
    public static final String DESCRIPTION_FIELD = "description";

    public static void addSongToPartyQueue(Context context, SongDTO song, String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION)
                .add(new FirebaseQueueRow(context, song));
    }

    public static void getAllParties(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebasePartyHelper.PARTY_COLLECTION)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getAllSongsForAParty(String partyName, OnSuccessListener<QuerySnapshot> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION)
                .get()
                .addOnSuccessListener(onSuccessListener);
    }

    public static void addParty(String partyName, String partyDescription) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> description = new HashMap<>();
        description.put(DESCRIPTION_FIELD, partyDescription);
        db.collection(PARTY_COLLECTION).document(partyName).set(description);
    }


    public static CollectionReference test(String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION);
    }

    public static CollectionReference partyTest() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(FirebasePartyHelper.PARTY_COLLECTION);
    }

}
