package com.example.jukebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jukebox.R;
import com.example.jukebox.utils.FirebasePartyHelper;

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

            if (!(partyName.isEmpty()) && !(partyDescription.isEmpty())) {
                addPartyIfItDoesNotAlreadyExist(partyName, partyDescription, v);
            } else {
                Toast.makeText(this, "Your party needs a name and description!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addPartyIfItDoesNotAlreadyExist(String partyName, String partyDescription, View v) {
        FirebasePartyHelper.getParty(partyName, documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Toast.makeText(this, "Party Name is taken, Sorry !!", Toast.LENGTH_LONG).show();
            } else {
                FirebasePartyHelper.addParty(partyName, partyDescription, this);
                startActivity(new Intent(AddPartyActivity.this, ChoosePartyActivity.class));
            }
        });
    }
}
