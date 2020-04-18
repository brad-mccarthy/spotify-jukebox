package com.example.jukebox.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jukebox.R;
import com.example.jukebox.service.SpotifyServiceClient;

import static com.example.jukebox.utils.SpotifyDataHelper.CLIENT_ID;
import static com.example.jukebox.utils.SpotifyDataHelper.REDIRECT_URI;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SpotifyServiceClient spotifyServiceClient = new SpotifyServiceClient();

        Button login = findViewById(R.id.login);
        login.setOnClickListener(loginOnClick(spotifyServiceClient));
    }

    private View.OnClickListener loginOnClick(SpotifyServiceClient spotifyServiceClient) {
        return view -> {
            WebView myWebView = new WebView(LoginActivity.this);
            setContentView(myWebView);
            myWebView.loadUrl(createAuthenticationUri());
        };
    }

    private String createAuthenticationUri() {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=" + CLIENT_ID + "&" +
                "response_type=code&" +
                "redirect_uri=" + REDIRECT_URI;
    }
}
