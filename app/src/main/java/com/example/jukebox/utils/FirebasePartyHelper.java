package com.example.jukebox.utils;

import android.content.Context;

import com.example.jukebox.model.FirebaseQueueRow;
import com.example.jukebox.model.song.SongDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.firestore.FieldValue.serverTimestamp;

public class FirebasePartyHelper {

    private static final String PARTY_COLLECTION = "party";
    private static final String SONG_COLLECTION = "songs";
    private static final String DESCRIPTION_FIELD = "description";
    public static final String QUEUE_POSITION_FIELD = "queuePosition";
    private static final String TIMESTAMP_FIELD = "timestamp";

    public static void addSongToPartyQueue(Context context, SongDTO song, String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION)
                .add(new FirebaseQueueRow(context, song));
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
        Map<String, Object> fields = getPartyFields(partyDescription);
        db.collection(PARTY_COLLECTION).document(partyName).set(fields);
    }

    private static Map<String, Object> getPartyFields(String partyDescription) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(DESCRIPTION_FIELD, partyDescription);
        fields.put(QUEUE_POSITION_FIELD, 0);
        fields.put(TIMESTAMP_FIELD, serverTimestamp());
        return fields;
    }


    public static CollectionReference getReferenceToSongs(String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION);
    }

    public static CollectionReference getReferenceToParties() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(FirebasePartyHelper.PARTY_COLLECTION);
    }

    public static void getParty(String partyName, OnSuccessListener<DocumentSnapshot> onSuccessListener) {
        getReferenceToParty(partyName)
                .get()
                .addOnSuccessListener(onSuccessListener);
    }

    public static DocumentReference getReferenceToParty(String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(FirebasePartyHelper.PARTY_COLLECTION)
                .document(partyName);
    }
}
