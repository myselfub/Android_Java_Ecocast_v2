package com.ecoplay.android.models.repository;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class LocalRepository {
    private static final String TAG = LocalRepository.class.getSimpleName();
    private final String PREFERENCE_NAME = "com.ecoplay.android.android_preferences";
    private final String DEFAULT_VALUE_STRING = "";
    private SharedPreferences sharedPreferences;

    private LocalRepository() {
    }

    public LocalRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setLoginInfo(String token, String email, String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.commit();
    }

    public Map<String, String> getLoginInfo() {
        String token = sharedPreferences.getString("token", null);
        String email = sharedPreferences.getString("email", null);
        String name = sharedPreferences.getString("name", null);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("email", email);
        map.put("name", name);

        return map;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.remove("email");
        editor.remove("name");
        editor.commit();
    }

    public void setLatLon(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latlon", value);
        editor.commit();
    }

    public String[] getLatLon() {
        String latlon = sharedPreferences.getString("latlon", null);
        if (latlon != null) {
            return latlon.split(",");
        } else {
            return null;
        }
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, DEFAULT_VALUE_STRING);
    }
}
