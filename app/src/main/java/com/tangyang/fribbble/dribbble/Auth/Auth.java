package com.tangyang.fribbble.dribbble.Auth;

import android.net.Uri;

/**
 * Created by tangy on 9/19/2016.
 */
public class Auth {
    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_ACCESS_TOKEN = "access_token";


    // use yours
    private static final String CLIENT_ID = "0f956bed9f5051a9e65e879c485fd84653f9c8d145c120af9df76f16b34bea82";

    // use yours
    private static final String CLIENT_SECRET = "82c7dd5f3e57fc968f7cfafa75fb0f5cbd202855b2783020b89dd677bccdfda1";

    // see http://developer.dribbble.com/v1/oauth/#scopes
    private static final String SCOPE = "public+write";

    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    public static final String REDIRECT_URI = "http://www.dribbbo.com";


    public static String getAuthorizeUrl() {
        String url = Uri.parse(URI_AUTHORIZE)
                        .buildUpon()
                        .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                        .build()
                        .toString();
        // fix encode issue, Uri.appendQueryParameter will automatically change "://" and "."
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        return url;
    }




}
