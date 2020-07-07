package com.iandroid.allclass.lib_baseimage.fresco;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by wangkm on 2019-10-28.
 */
public interface FrescoImageDownCallBack {
    void onRequestSuccess(ImageView view, String url, File ImageFile);
    void onRequestFailure(ImageView view, String url);
}
