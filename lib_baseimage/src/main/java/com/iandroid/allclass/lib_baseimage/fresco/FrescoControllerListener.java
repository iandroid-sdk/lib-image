package com.iandroid.allclass.lib_baseimage.fresco;

import android.graphics.drawable.Animatable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.imagepipeline.image.ImageInfo;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;

public class FrescoControllerListener implements ControllerListener {
    private boolean autoPlay = true;
    private int loopTimes;
    private float aspectRatio = 0.0f;
    private FrescoAnimationListener animationListener;
    private WeakReference<ImageView> imageViewWeakReference;

    public FrescoControllerListener(FrescoAnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public FrescoControllerListener() {
    }

    public void setImageView(ImageView imageView) {
        if (imageView != null)
            imageViewWeakReference = new WeakReference<>(imageView);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setLoopTimes(int loopTimes) {
        this.loopTimes = loopTimes;
    }

    @Override
    public void onSubmit(String id, Object callerContext) {

    }

    @Override
    public void onFinalImageSet(String id, Object info, Animatable animatable) {
        ImageView imageView = imageViewWeakReference != null ? imageViewWeakReference.get() : null;
        if (imageView != null && info instanceof ImageInfo) {
            ImageInfo imageInfo = (ImageInfo) info;
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            if (layoutParams != null && imageInfo.getWidth() > 0 && imageInfo.getHeight() > 0) {

                if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
                        && layoutParams.height > 0
                        && aspectRatio == 0.0f
                        && imageInfo.getHeight() > 0) {
                    layoutParams.width = (imageInfo.getWidth() * layoutParams.height) / imageInfo.getHeight();
                } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT
                        && layoutParams.width > 0
                        && aspectRatio == 0.0f
                        && imageInfo.getWidth() > 0) {
                    layoutParams.height = (imageInfo.getHeight() * layoutParams.width) / imageInfo.getWidth();
                } else {
                    if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && aspectRatio >= 0) {
                        layoutParams.width = imageInfo.getWidth();
                    }
                    if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT && aspectRatio >= 0) {
                        layoutParams.height = imageInfo.getHeight();
                    }
                }

                imageView.setLayoutParams(layoutParams);
                if (imageView instanceof SimpleDraweeView
                        && imageInfo.getHeight() > 0
                        && imageInfo.getWidth() > 0
                        && aspectRatio < 0)
                    ((SimpleDraweeView) imageView).setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
            }
        }

        if (animatable != null) {
            if (animatable instanceof Animatable) {
                AnimatedDrawable2 animatedDrawable = (AnimatedDrawable2) animatable;
                if (animationListener != null)
                    animatedDrawable.setAnimationListener(animationListener);
                if (loopTimes > 0) {
                    animatedDrawable.setAnimationBackend(new FrescoLoopCountBackend(animatedDrawable.getAnimationBackend(), loopTimes));
                    if (autoPlay) animatable.start();
                }
            }
        }

    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable Object imageInfo) {

    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable) {

    }

    @Override
    public void onFailure(String id, Throwable throwable) {

    }

    @Override
    public void onRelease(String id) {

    }
}
