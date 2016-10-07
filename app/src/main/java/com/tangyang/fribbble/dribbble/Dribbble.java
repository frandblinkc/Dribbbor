package com.tangyang.fribbble.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tangyang.fribbble.model.Bucket;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.model.User;
import com.tangyang.fribbble.utils.ModelUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tangy on 9/22/2016.
 */
public class Dribbble {

    public static final int SHOTS_PER_PAGE = 20;
    public static final int BUCKETS_PER_PAGE = 20;

    private static final String TAG = "Dribbble API";

    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String USER_END_POINT = API_URL + "user";
    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";

    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SHOT_ID = "shot_id";


    private static final TypeToken<User> USER_TYPE_TOKEN = new TypeToken<User>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE_TOKEN = new TypeToken<Bucket>() {};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE_TOKEN = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE_TOKEN = new TypeToken<List<Bucket>>(){};

    private static String accessToken;
    private static User user;

    private static OkHttpClient client = new OkHttpClient();

    // =========== private helper methods ===================================
    // helper methods to make HTTP requests

    // make a request builder with accessToken in header
    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeRequest(Request request) throws DribbbleException {
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, response.header("X-RateLimit-Remaining"));
            return response;
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }
    }

    private static Response makeGetRequest(String url) throws  DribbbleException{
        Request request = authRequestBuilder(url).build();
        return makeRequest(request);
    }

    private static Response makePostRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                            .post(requestBody)
                            .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                            .put(requestBody)
                            .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
        return makeRequest(request);
    }

    private static <T> T parseResponse(Response response,
                                       TypeToken<T> typeToken) throws DribbbleException{
        String responseString;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }
        Log.d(TAG, responseString);

        try {
            return ModelUtils.toObject(responseString, typeToken);
        } catch (JsonSyntaxException e) {
            throw new DribbbleException(e.getMessage());
        }
    }

    // Check if put, post, delete request successful, since Dribbble API returns 204: No Content when normal
    private static void checkStatusCode(Response response, int statusCode) throws DribbbleException{
        if (response.code() != statusCode) {
            throw new DribbbleException(response.message());
        }
    }

    //============== public methods ============================================

    // initialize, load access token
    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    // store token
    public static void storeAccessToken(@NonNull Context context, String token) {
        SharedPreferences sp = context.getApplicationContext()
                                       .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    // check login status
    public static boolean isLoggedIn() { return accessToken != null; }

    // store user
    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    // load user
    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, USER_TYPE_TOKEN);
    }

    public static void login(@NonNull Context context, @NonNull String accessToken)
                                throws DribbbleException {
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);

        Dribbble.user = getUser();
        storeUser(context, user);
    }

    public static void logout(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);

        accessToken = null;
        user = null;

        clearCookies(context);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >= " + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush(); // force a sync
        } else {
            Log.d(TAG, "Using clearCookies code for API < " + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieSyncManager.startSync();
            // remove cookie in RAM
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();

            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }


    // get user from Dribbble.com
    public static User getUser() throws DribbbleException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE_TOKEN);
    }

    // get current user stored in memory
    public static User getCurrentUser () {
        return user;
    }

    // get shots from Dribbble.com
    public static List<Shot> getShots(int page) throws DribbbleException {
        Log.d("frandblinkc", "inside Dribbble.getShots");
        String url = SHOTS_END_POINT + "?page=" + page + "&per_page=" + SHOTS_PER_PAGE;

        Log.d("frandblinkc", "the url is: " + url);

        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE_TOKEN);
    }

    // get shots in bucket
    public static List<Shot> getBucketShots(String bucketId, int page) throws DribbbleException {
        Log.d("frandblinkc", "inside Dribbble.getBucketShots");
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots?page=" + page + "&per_page=" + SHOTS_PER_PAGE;
        Log.d("frandblinkc", "the url is: " + url);
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE_TOKEN);
    }



    // get all of  current user's buckets from Dribbble.com
    public static List<Bucket> getUserBuckets() throws DribbbleException {
        String url = USER_END_POINT + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE_TOKEN);
    }

    // get current user's buckets from Dribbble.com
    public static List<Bucket> getUserBuckets(int page) throws DribbbleException {
        String url = USER_END_POINT + "/buckets?page=" + page + "&per_page=" + BUCKETS_PER_PAGE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE_TOKEN);
    }

    // get all buckets that contains the shotId
    public static List<Bucket> getShotBuckets(String shotId) throws  DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE_TOKEN);
    }


    public static Bucket newBucket(@NonNull String name, @NonNull String description)
                                                            throws DribbbleException {
        FormBody formBody = new FormBody.Builder()
                                    .add(KEY_NAME, name)
                                    .add(KEY_DESCRIPTION, description)
                                    .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE_TOKEN);
    }

    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws  DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                                .add(KEY_SHOT_ID, shotId)
                                .build();
        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void removeBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws  DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();
        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

}
