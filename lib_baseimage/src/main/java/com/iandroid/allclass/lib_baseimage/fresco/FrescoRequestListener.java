package com.iandroid.allclass.lib_baseimage.fresco;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.iandroid.allclass.lib_baseimage.ImageLoader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

public class FrescoRequestListener implements RequestListener {
    private FrescoImageDownCallBack imageDownCallBack;
    private String url;
    private WeakReference<ImageView> imageViewWeakReference = null;

    public FrescoRequestListener(ImageView imageView, String url, FrescoImageDownCallBack imageDownCallBack) {
        this.imageDownCallBack = imageDownCallBack;
        this.url = url;
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    public void onRequestStart(ImageRequest request, Object callerContext, String requestId, boolean isPrefetch) {

    }

    @Override
    public void onRequestSuccess(ImageRequest request, String requestId, boolean isPrefetch) {
        if (imageDownCallBack != null) {
            File file = TextUtils.isEmpty(url) ? null : ImageLoader.getCachedImageOnDisk(Uri.parse(url));
            imageDownCallBack.onRequestSuccess(getView(), url, file);
        }
    }

    @Override
    public void onRequestFailure(ImageRequest request, String requestId, Throwable throwable, boolean isPrefetch) {
        if (imageDownCallBack != null)
            imageDownCallBack.onRequestFailure(getView(), url);
    }

    private ImageView getView() {
        return imageViewWeakReference != null ? imageViewWeakReference.get() : null;
    }

    @Override
    public void onRequestCancellation(String requestId) {

    }

    @Override
    public void onProducerStart(String requestId, String producerName) {

    }

    @Override
    public void onProducerEvent(String requestId, String producerName, String eventName) {

    }

    @Override
    public void onProducerFinishWithSuccess(String requestId, String producerName, Map<String, String> extraMap) {

    }

    @Override
    public void onProducerFinishWithFailure(String requestId, String producerName, Throwable t, Map<String, String> extraMap) {

    }

    @Override
    public void onProducerFinishWithCancellation(String requestId, String producerName, Map<String, String> extraMap) {

    }

    @Override
    public void onUltimateProducerReached(String requestId, String producerName, boolean successful) {

    }

    @Override
    public boolean requiresExtraMap(String requestId) {
        return false;
    }
}
