package com.iandroid.allclass.lib_baseimage;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

public class IImageLoader {
    //initialize imagelib's congif
    public void init(Context context, FrescoConfig frescoConfig) {
    }

    //clear memory cache
    public void clearMemoryCaches() {
    }

    public void displayImage(View imageView, String url, ImageOptions imageOptions) {
    }

    public void displayImage(SimpleDraweeView imageView, Uri uri, ImageOptions imageOptions) {
    }

    public void displayImage(View view, int resID, ImageOptions imageOptions) {
    }

    public void clearMemoryCacheByUrl(String url) {
        if (!TextUtils.isEmpty(url))
            clearMemoryCacheByUri(Uri.parse(url));
    }

    public void clearMemoryCacheByUri(Uri uri) {
    }

    public void clearMemoryCacheByTag(String tag) {
    }
}
