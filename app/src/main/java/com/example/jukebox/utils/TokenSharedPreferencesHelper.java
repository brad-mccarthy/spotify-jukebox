package com.example.jukebox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TokenSharedPreferencesHelper {

    public static final String EXPIRES_ON = "expiresOn";
    public static final String TOKEN_PREF_NAME = "token";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static Clock clock = Clock.systemDefaultZone();

    public static String getAccessToken(Context context) {

        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);

        String accessToken = tokenPreferences.getString(ACCESS_TOKEN, null);

        if (accessToken == null) {
            throw new RuntimeException("No access token available");
        } else {
            return accessToken;
        }
    }

    public static void updateAccessToken(Context context, String accessToken, Integer expiresIn) {
        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);
        long testing = calculateExpiryTimeFromNow(expiresIn);
        tokenPreferences.edit()
                .putString(ACCESS_TOKEN, accessToken)
                .putLong(EXPIRES_ON, testing)
                .apply();

    }

    public static void updateAccessToken(Context context, String accessToken, String refreshToken, Integer expiresIn) {
        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);
        tokenPreferences.edit()
                .putString(ACCESS_TOKEN, accessToken)
                .putString(REFRESH_TOKEN, refreshToken)
                .putLong(EXPIRES_ON, calculateExpiryTimeFromNow(expiresIn))
                .apply();

    }

    public static String getRefreshToken(Context context) {
        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);

        String refreshToken = tokenPreferences.getString(REFRESH_TOKEN, null);

        if (refreshToken == null) {
            throw new RuntimeException("No refresh token available");
        } else {
            return refreshToken;
        }
    }

    public static boolean isAccessTokenExpired(Context context) {
        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);

        return tokenPreferences.getLong(EXPIRES_ON, 0) <= toEpochMilli(LocalDateTime.now(clock)) ;
    }

    public static boolean accessCodeExists(Context context) {
        SharedPreferences tokenPreferences = context.getSharedPreferences(TOKEN_PREF_NAME, Context.MODE_PRIVATE);

        String accessToken = tokenPreferences.getString(ACCESS_TOKEN, null);

        return accessToken == null;
    }

    private static long calculateExpiryTimeFromNow(Integer expiresIn) {
        return LocalDateTime.now(clock)
                .plusSeconds(expiresIn)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }


}
