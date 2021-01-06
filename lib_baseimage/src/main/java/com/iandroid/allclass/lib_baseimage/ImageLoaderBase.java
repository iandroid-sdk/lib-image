package com.iandroid.allclass.lib_baseimage;


import com.iandroid.allclass.lib_baseimage.fresco.FrescoLoader;

public class ImageLoaderBase {
    public enum ImageLoaderType {
        image_lib_fresco
    }

    private IImageLoader frescoImp;
    private IImageLoader picasso;//保留

    private static class LazyHolder {
        private static final ImageLoaderBase sInstance = new ImageLoaderBase();
    }

    public static ImageLoaderBase getInstance() {
        return ImageLoaderBase.LazyHolder.sInstance;
    }

    public IImageLoader get(ImageLoaderType type) {
        if (type == ImageLoaderType.image_lib_fresco) {
            if (frescoImp == null) {
                frescoImp = new FrescoLoader();
            }
            return frescoImp;
        }
        return null;
    }
}
