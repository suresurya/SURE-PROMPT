package com.suresurya.sureprompt.network;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "jwt_refresh_token";
    
    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback to normal shared preferences if encryption fails (rare)
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public void saveRefreshToken(String token) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_REFRESH_TOKEN).apply();
    }
}
