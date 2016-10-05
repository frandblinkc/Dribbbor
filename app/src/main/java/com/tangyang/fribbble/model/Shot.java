package com.tangyang.fribbble.model;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.Map;

/**
 * Created by tangy on 9/13/2016.
 */
public class Shot {
    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_HIDPI = "hidpi";


    public String id;
    public String title;
    public String description;
    public String html_url;

    public int width;
    public int height;
    public Map<String, String> images;
    public boolean animated;

    public int views_count;
    public int likes_count;
    public int buckets_count;

    public Date createdAt;
    public User user;

    public boolean liked;
    public boolean bucketed;

    @Nullable
    public String getImageUrl() {

        if (images == null) {
            return null;
        }

        if (images.containsKey(IMAGE_HIDPI) && images.get(IMAGE_HIDPI) != null) {
            return images.get(IMAGE_HIDPI);
        } else if (images.containsKey(IMAGE_NORMAL) && images.get(IMAGE_NORMAL) != null) {
            return images.get(IMAGE_NORMAL);
        }
        return null;
    }


}
