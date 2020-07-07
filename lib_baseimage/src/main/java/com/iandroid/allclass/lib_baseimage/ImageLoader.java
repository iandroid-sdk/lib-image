package com.iandroid.allclass.lib_baseimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.iandroid.allclass.lib_baseimage.fresco.FrescoImageDownCallBack;
import com.iandroid.allclass.lib_baseimage.fresco.FrescoRequestListener;
import com.iandroid.allclass.lib_commonutils.DeviceUtil;

import java.io.File;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;


/**
 * wangkm
 */
public class ImageLoader extends ImageLoaderBase {
    public static final String TAG_ROOM = "room";

    //初始化
    public static void init(Context context, FrescoConfig frescoConfig) {
        getInstance().get(ImageLoaderType.image_lib_fresco).init(context, frescoConfig);
    }

    //清理缓存
    public static void clearMemoryCaches() {
        getInstance().get(ImageLoaderType.image_lib_fresco).clearMemoryCaches();
    }

    //深度清理
    public static void deepClearMemoryCaches(){
        Fresco.getImagePipeline().clearMemoryCaches();
        Fresco.getImagePipelineFactory().getBitmapCountingMemoryCache().clear();
    }

    //加载图片
    public static void displayImage(View view, String url) {
        if (view == null) {
            return;
        }
        if (view instanceof SimpleDraweeView) {
            getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(view, url, null);
        }
    }

    /**
     * 显示固定尺寸（200dp）的图片，支持压缩
     *
     * @param view
     * @param url
     * @param imageOptions
     */
    public static void displayFixsizeImage(SimpleDraweeView view, String url, ImageOptions imageOptions) {
        if (view != null) {
            int imageSize = DeviceUtil.dip2px(view.getContext(), 200);
            if (imageOptions == null)
                imageOptions = new ImageOptions.Builder().scaleWidth(imageSize).scaleHeight(imageSize).build();
            else {
                imageOptions.setScaleheight(imageSize);
                imageOptions.setScalewidth(imageSize);
            }
        }
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(view, url, imageOptions);
    }

    public static void displayImage(SimpleDraweeView view, String url) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(view, url, null);
    }

    public static void displayImage(SimpleDraweeView view, String url, String tag) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(view, url, new ImageOptions.Builder().tag(tag).build());
    }

    public static void displayImage(SimpleDraweeView view, String url, ImageOptions imageOptions) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(view, url, imageOptions);
    }

    public static void clearMemoryCacheByUrl(String url) {
        getInstance().get(ImageLoaderType.image_lib_fresco).clearMemoryCacheByUrl(url);
    }

    public void clearMemoryCacheByUri(Uri uri) {
        getInstance().get(ImageLoaderType.image_lib_fresco).clearMemoryCacheByUri(uri);
    }

    public static void clearMemoryCacheByTag(String tag) {
        getInstance().get(ImageLoaderType.image_lib_fresco).clearMemoryCacheByTag(tag);
    }


    public static void displayImage(SimpleDraweeView imageView, @DrawableRes int ResID, ImageOptions imageOptions) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(imageView, ResID, imageOptions);
    }


    public static void displayImage(View imageView, @DrawableRes int resID) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(imageView, resID, null);
    }

    public static void setImageResource(View imageView, @DrawableRes int resID) {
        if (imageView == null)
            return;
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setImageResource(resID);
        }
    }

    public static void displayImage(SimpleDraweeView imageView, Uri uri) {
        displayImage(imageView, uri, null);
    }

    public static void displayImage(SimpleDraweeView imageView, Uri uri, ImageOptions imageOptions) {
        getInstance().get(ImageLoaderType.image_lib_fresco).displayImage(imageView, uri, imageOptions);
    }

    public static void displayHeadImage(SimpleDraweeView view, String url) {
        displayHeadImage(view, url, 0);
    }

    public static void displayHeadImage(SimpleDraweeView view, String url, @DimenRes int id) {
        int size = 0;
        if (id != 0)
            size = view.getContext().getResources().getDimensionPixelSize(id);
        if (size == 0)
            size = DeviceUtil.dip2px(view.getContext(), 32);

        if (view != null) {
            displayImage(view, url, new ImageOptions.Builder().scaleHeight(size).scaleWidth(size).build());
        }
    }


    /**
     * 支持仅加载图片不渲染至ImageView的后处理（类似图片加载完成后需要后处理在渲染至ImageView）
     * 加载图片完成之后，会通过FrescoImageDownCallBack 回调应用层
     * @param imageView
     * @param url
     * @param imageDownCallBack
     */
    public static void loadImage(ImageView imageView, String url, FrescoImageDownCallBack imageDownCallBack) {
        if (TextUtils.isEmpty(url)) {
            if (imageDownCallBack != null)
                imageDownCallBack.onRequestFailure(imageView, url);
            return;
        }

        Uri uri = Uri.parse(url);
        File file = getCachedImageOnDisk(uri);
        if (file != null
                && file.exists()) {
            if (imageDownCallBack != null) {
                imageDownCallBack.onRequestSuccess(imageView, url, file);
            }
            return;
        }

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .setRequestListener(new FrescoRequestListener(imageView, url, imageDownCallBack))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.fetchDecodedImage(request, null);
    }

    public static File getCachedImageOnDisk(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                    .getEncodedCacheKey(ImageRequest.fromUri(loadUri), null);

            ImagePipelineFactory pipelineFactory = ImagePipelineFactory.getInstance();
            try {
                if (pipelineFactory.getMainFileCache().hasKey(cacheKey)) {
                    BinaryResource resource = pipelineFactory.getMainFileCache().getResource(cacheKey);
                    localFile = ((FileBinaryResource) resource).getFile();
                } else if (pipelineFactory.getSmallImageFileCache().hasKey(cacheKey)) {
                    BinaryResource resource = pipelineFactory.getSmallImageFileCache().getResource(cacheKey);
                    localFile = ((FileBinaryResource) resource).getFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return localFile;
    }

    public static Bitmap loadImage(String url, FrescoImageDownCallBack imageDownCallBack) {
        if (TextUtils.isEmpty(url))
            return null;

        Uri uri = Uri.parse(url);
        File file = getCachedImageOnDisk(uri);
        if (file != null
                && file.exists()) {
            Bitmap loadbitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (loadbitmap != null)
                return loadbitmap;
        }

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .setRequestListener(new FrescoRequestListener(null, url, imageDownCallBack))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.fetchDecodedImage(request, null);
        return null;
    }
}
