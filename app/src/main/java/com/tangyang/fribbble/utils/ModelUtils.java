package com.tangyang.fribbble.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Created by tangy on 9/22/2016.
 */
public class ModelUtils {
    private static Gson gson = new Gson();
    private static String PREF_NAME = "models";

    public static <T> String toString(T object, TypeToken<T> typeToken) {
        return gson.toJson(object, typeToken.getType());
    }

    public static <T> T toObject(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    // save object to shared preferences "models"
    public static void save(Context context, String key, Object object) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, gson.toJson(object)).apply();
    }

    public static <T> T read(Context context, String key, TypeToken<T> typeToken) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            return gson.fromJson(sp.getString(key, null), typeToken.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
