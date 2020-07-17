package com.example.jukebox.utils;

import android.content.Context;

import com.example.jukebox.model.FirebaseQueueRow;
import com.example.jukebox.model.song.SongDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
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
    public static final String HOST_FIELD = "host";

    public static void addSongToPartyQueue(Context context, SongDTO song, String partyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION)
                .add(new FirebaseQueueRow(context, song));
    }

    public static ListenerRegistration getAllSongsForAParty(String partyName, EventListener<QuerySnapshot> snapshotEventListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(PARTY_COLLECTION)
                .document(partyName)
                .collection(SONG_COLLECTION)
                .addSnapshotListener(snapshotEventListener);
    }

    public static void addParty(String partyName, String partyDescription, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> fields = getPartyFields(partyDescription, context);
        db.collection(PARTY_COLLECTION).document(partyName).set(fields);
    }

    private static Map<String, Object> getPartyFields(String partyDescription, Context context) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(DESCRIPTION_FIELD, partyDescription);
        fields.put(QUEUE_POSITION_FIELD, 0);
        fields.put(TIMESTAMP_FIELD, serverTimestamp());
        fields.put(HOST_FIELD, SpotifyDataHelper.getCurrentUsername(context));
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
