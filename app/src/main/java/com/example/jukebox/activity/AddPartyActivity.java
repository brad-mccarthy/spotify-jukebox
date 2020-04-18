package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jukebox.R;
import com.example.jukebox.utils.FirebasePartyHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPartyActivity extends AppCompatActivity {

    private Button addPartyButton;
    private EditText partyNameInput;
    private EditText partyDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);

        addPartyButton = findViewById(R.id.add_party_button);
        partyNameInput = findViewById(R.id.party_name_input);
        partyDescriptionInput = findViewById(R.id.party_description);

        addPartyButton.setOnClickListener(v -> {
            String partyName = partyNameInput.getText().toString();
            String partyDescription = partyDescriptionInput.getText().toString();

            if (!(partyName.isEmpty()) && !(partyDescription.isEmpty())){
                FirebasePartyHelper.addParty(partyName, partyDescription);
                startActivity(new Intent(AddPartyActivity.this, ChoosePartyActivity.class));
            } else {
                Snackbar.make(v, "Your party needs a name and description!", Snackbar.LENGTH_LONG);
            }
        });
    }


}
