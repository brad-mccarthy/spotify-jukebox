package com.example.jukebox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static com.example.jukebox.utils.TokenSharedPreferencesHelper.ACCESS_TOKEN;
import static com.example.jukebox.utils.TokenSharedPreferencesHelper.EXPIRES_ON;
import static com.example.jukebox.utils.TokenSharedPreferencesHelper.REFRESH_TOKEN;
import static com.example.jukebox.utils.TokenSharedPreferencesHelper.TOKEN_PREF_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

//import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class TokenSharedPreferencesHelperUnitTest {

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Before
    public void setup() {
        initMocks(this);
        given(context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE)).willReturn(sharedPreferences);

    }

    @Test
    public void shouldRetrieveAccessTokenWhenItExists() {
        String expectedToken = "riehgskdfnsdg";
        given(sharedPreferences.getString(ACCESS_TOKEN, null)).willReturn(expectedToken);

        String actualToken = TokenSharedPreferencesHelper.getAccessToken(context);

        assertThat(actualToken, is(expectedToken));
    }

    @Test
    public void shouldThrowExceptionWhenAccessTokenDoesNotExist() {
        given(sharedPreferences.getString(ACCESS_TOKEN, null)).willReturn(null);

        assertThrows(RuntimeException.class, () -> TokenSharedPreferencesHelper.getAccessToken(context), "No access token available");
    }

    @Test
    public void shouldUpdateAccessTokenSuccessfully() {
        given(sharedPreferences.edit()).willReturn(sharedPreferencesEditor);
        given(sharedPreferencesEditor.putString(anyString(), anyString())).willReturn(sharedPreferencesEditor);
        given(sharedPreferencesEditor.putLong(anyString(), anyInt())).willReturn(sharedPreferencesEditor);

        Instant currentTime = Instant.ofEpochMilli(123456);
        TokenSharedPreferencesHelper.clock = Clock.fixed(currentTime, ZoneId.systemDefault());

        TokenSharedPreferencesHelper.updateAccessToken(context, "abcd", 3600);

        verify(sharedPreferencesEditor).putString(ACCESS_TOKEN, "abcd");
        verify(sharedPreferencesEditor).putLong(EXPIRES_ON, currentTime.plus(Duration.ofSeconds(3600)).toEpochMilli());
    }

    @Test
    public void shouldUpdateAccessTokenSuccessfullyWithRefreshToken() {
        given(sharedPreferences.edit()).willReturn(sharedPreferencesEditor);
        given(sharedPreferencesEditor.putString(anyString(), anyString())).willReturn(sharedPreferencesEditor);
        given(sharedPreferencesEditor.putLong(anyString(), anyInt())).willReturn(sharedPreferencesEditor);

        Instant currentTime = Instant.ofEpochMilli(123456);
        TokenSharedPreferencesHelper.clock = Clock.fixed(currentTime, ZoneId.systemDefault());

        TokenSharedPreferencesHelper.updateAccessToken(context, "abcd", "refresh", 3600);

        verify(sharedPreferencesEditor).putString(ACCESS_TOKEN, "abcd");
        verify(sharedPreferencesEditor).putString(REFRESH_TOKEN, "refresh");
        verify(sharedPreferencesEditor).putLong(EXPIRES_ON, currentTime.plus(Duration.ofSeconds(3600)).toEpochMilli());
    }

    @Test
    public void shouldRecogniseWhenAccessTokenIsExpired() {
        Instant currentTime = Instant.ofEpochMilli(123456);
        TokenSharedPreferencesHelper.clock = Clock.fixed(currentTime, ZoneId.systemDefault());

        given(sharedPreferences.getLong(EXPIRES_ON, 0)).willReturn(currentTime.plus(Duration.ofSeconds(1)).toEpochMilli());

        boolean accessTokenExpired = TokenSharedPreferencesHelper.isAccessTokenExpired(context);

        assertThat(accessTokenExpired, is(false));
    }

    @Test
    public void shouldRecogniseWhenAccessTokenIsNotExpired() {
        Instant currentTime = Instant.ofEpochMilli(123456);
        TokenSharedPreferencesHelper.clock = Clock.fixed(currentTime, ZoneId.systemDefault());

        given(sharedPreferences.getLong(EXPIRES_ON, 0)).willReturn(currentTime.minus(Duration.ofSeconds(1)).toEpochMilli());

        boolean accessTokenExpired = TokenSharedPreferencesHelper.isAccessTokenExpired(context);

        assertThat(accessTokenExpired, is(true));
    }


}