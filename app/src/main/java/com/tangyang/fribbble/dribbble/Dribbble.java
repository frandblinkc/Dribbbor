package com.tangyang.fribbble.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by tangy on 9/22/2016.
 */
public class Dribbble {

    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String SP_AUTH = "auth";

    private static String accessToken;

    // load access token
    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);

    }

    public static void storeAccessToken(@NonNull Context context, String token) {
        SharedPreferences sp = context.getApplicationContext()
                                       .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext()
                                        .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static boolean isLoggedIn() { return accessToken != null; }
}
