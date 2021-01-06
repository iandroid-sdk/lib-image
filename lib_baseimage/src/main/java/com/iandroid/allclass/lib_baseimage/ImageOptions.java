package com.iandroid.allclass.lib_baseimage;


import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.iandroid.allclass.lib_baseimage.fresco.FrescoControllerListener;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

public class ImageOptions {
    private float aspectRatio;
    //view的宽高
    private int width;
    private int height;
    //压缩后的宽度
    private int scalewidth;
    //压缩后的高度
    private int scaleheight;
    private int blur_radius;
    private int blur_iterations;
    private String tag;
    private int loopTimes;
    private boolean auto_play = true;
    private boolean asCircle = false;
    private boolean progressiveRenderingEnabled = false;
    private BasePostprocessor basePostprocessor;

    private FrescoControllerListener ctlListener;
    private ScalingUtils.ScaleType actualScaleType;
    //边框
    private @ColorRes
    int overlayColor = 0;
    private @ColorRes
    int border_color = 0;
    private float border_size = 0.0f;

    //圆角
    private float left_top_corner_radius = 0.0f;
    private float left_bottom_corner_radius = 0.0f;
    private float right_top_corner_radius = 0.0f;
    private float right_bottom_corner_radius = 0.0f;
    //展位图
    private @DrawableRes
    int placeholderImage = 0;
    private ScalingUtils.ScaleType placeholderScaleType = null;
    //异常图
    private @DrawableRes int failureImage = 0;
    private ScalingUtils.ScaleType failureScaleType = ScalingUtils.ScaleType.FIT_XY;

    private boolean isNeedBlackWhite = false;

    public boolean isProgressiveRenderingEnabled() {
        return progressiveRenderingEnabled;
    }

    public void setCtlListener(FrescoControllerListener ctlListener) {
        this.ctlListener = ctlListener;
    }

    public BasePostprocessor getBasePostprocessor() {
        return basePostprocessor;
    }

    public void setBasePostprocessor(BasePostprocessor basePostprocessor) {
        this.basePostprocessor = basePostprocessor;
    }

    public ScalingUtils.ScaleType getActualScaleType() {
        return actualScaleType ;
    }

    public int getBlur_radius() {
        return blur_radius;
    }

    public int getBlur_iterations() {
        return blur_iterations;
    }

    public void setBlur_iterations(int blur_iterations) {
        this.blur_iterations = blur_iterations;
    }

    public String getTag() {
        return tag;
    }

    public int getScalewidth() {
        return scalewidth;
    }

    public void setScalewidth(int scalewidth) {
        this.scalewidth = scalewidth;
    }

    public int getScaleheight() {
        return scaleheight;
    }

    public void setScaleheight(int scaleheight) {
        this.scaleheight = scaleheight;
    }

    public int getLoopTimes() {
        return loopTimes;
    }

    public ResizeOptions getResizeOptions() {
        if (scalewidth > 0 && scaleheight > 0)
            return new ResizeOptions(scalewidth, scaleheight);
        return null;
    }

    public boolean isAsCircle() {
        return asCircle;
    }

    public int getBorder_color() {
        return border_color;
    }

    public float getBorder_size() {
        return border_size;
    }

    public float getLeft_top_corner_radius() {
        return left_top_corner_radius;
    }

    public float getLeft_bottom_corner_radius() {
        return left_bottom_corner_radius;
    }

    public float getRight_top_corner_radius() {
        return right_top_corner_radius;
    }

    public float getRight_bottom_corner_radius() {
        return right_bottom_corner_radius;
    }

    public int getOverlayColor() {
        return overlayColor;
    }

    public int getPlaceholderImage() {
        return placeholderImage;
    }

    public ScalingUtils.ScaleType getPlaceholderScaleType() {
        return placeholderScaleType == null ? ScalingUtils.ScaleType.FIT_XY : placeholderScaleType;
    }

    public int getFailureImage() {
        return failureImage;
    }

    public ScalingUtils.ScaleType getFailureScaleType() {
        return failureScaleType == null ? ScalingUtils.ScaleType.FIT_XY : failureScaleType;
    }

    public boolean isHasCorver() {
        return left_bottom_corner_radius > 0
                || left_top_corner_radius > 0
                || right_bottom_corner_radius > 0
                || right_top_corner_radius > 0;
    }
    public boolean isAuto_play() {
        return auto_play;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public boolean isNeedBlackWhite() {
        return isNeedBlackWhite;
    }

    public FrescoControllerListener getCtlListener() {
        return ctlListener;
    }

    private ImageOptions(Builder builder) {
        this.isNeedBlackWhite = builder.isNeedBlackWhite;
        this.basePostprocessor = builder.basePostprocessor;
        this.overlayColor = builder.overlayColor;
        this.scaleheight = builder.scaleheight;
        this.scalewidth = builder.scalewidth;
        this.blur_radius = builder.blur_radius;
        this.blur_iterations = builder.blur_iterations;
        this.tag = builder.tag;
        this.loopTimes = builder.loopTimes;
        this.auto_play = builder.auto_play;
        this.ctlListener = builder.ctlListener;
        this.actualScaleType = builder.actualScaleType;
        this.asCircle = builder.asCircle;
        this.progressiveRenderingEnabled = builder.progressiveRenderingEnabled;
        this.border_color = builder.border_color;
        this.border_size = builder.border_size;
        this.left_top_corner_radius = builder.left_top_corner_radius;
        this.left_bottom_corner_radius = builder.left_bottom_corner_radius;
        this.right_top_corner_radius = builder.right_top_corner_radius;
        this.right_bottom_corner_radius = builder.right_bottom_corner_radius;
        this.placeholderImage = builder.placeholderImage;
        this.placeholderScaleType = builder.placeholderScaleType;

        this.failureImage = builder.failureImage;
        this.failureScaleType = builder.failureScaleType;
        this.height = builder.height;
        this.width = builder.width;
        this.aspectRatio = builder.aspectRatio;
    }

    public static class Builder {
        private float aspectRatio;//-1.0表示动态计算aspectRatio
        //view的宽高
        private int width;
        private int height;
        //压缩后的宽度
        private int scalewidth;
        //压缩后的高度
        private int scaleheight;
        //模糊图1-100
        private int blur_radius;
        //模糊图迭代次数 越大越魔化
        private int blur_iterations;
        //图片的tag，ImageLoader.clearMemoryCacheByTag 使用
        private String tag;
        //小于等于0，标识一直循环，否则表示循环次数
        private int loopTimes;
        //是否自动播放动画
        private boolean auto_play = true;
        //ControllerListener监听回调
        private FrescoControllerListener ctlListener;
        //图片裁剪方式
        private ScalingUtils.ScaleType actualScaleType = null;
        //是否支持圆圈
        private boolean asCircle = false;
        //渐进式
        private boolean progressiveRenderingEnabled = false;
        private boolean isNeedBlackWhite = false;

        //边框
        private @ColorRes
        int overlayColor = 0;
        private @ColorRes
        int border_color = 0;
        private float border_size = 0.0f;

        //圆角
        private float left_top_corner_radius = 0.0f;
        private float left_bottom_corner_radius = 0.0f;
        private float right_top_corner_radius = 0.0f;
        private float right_bottom_corner_radius = 0.0f;

        private BasePostprocessor basePostprocessor;

        //展位图
        private @DrawableRes
        int placeholderImage = 0;
        private ScalingUtils.ScaleType placeholderScaleType = ScalingUtils.ScaleType.FIT_XY;

        //异常图
        private @DrawableRes
        int failureImage = 0;
        private ScalingUtils.ScaleType failureScaleType = ScalingUtils.ScaleType.FIT_XY;

        public Builder aspectRatio(float aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder isNeedBlackWhite(boolean isNeedBlackWhite) {
            this.isNeedBlackWhite = isNeedBlackWhite;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder basePostprocessor(BasePostprocessor basePostprocessor) {
            this.basePostprocessor = basePostprocessor;
            return this;
        }

        public Builder failureImage(@DrawableRes int failureImage) {
            this.failureImage = failureImage;
            return this;
        }

        public Builder failureImage(@DrawableRes int failureImage, ScalingUtils.ScaleType failureScaleType) {
            this.failureImage = failureImage;
            this.failureScaleType = failureScaleType;
            return this;
        }


        public Builder placeholderImage(@DrawableRes int placeholderImage){
            this.placeholderImage = placeholderImage;
            return this;
        }

        public Builder placeholderImage(@DrawableRes int placeholderImage, ScalingUtils.ScaleType placeholderScaleType){
            this.placeholderImage = placeholderImage;
            this.placeholderScaleType = placeholderScaleType;
            return this;
        }

        public Builder overlayColor(@ColorRes int overlayColor){
            this.overlayColor = overlayColor;
            return this;
        }

        public Builder cornerRadius(float cornerRadius) {
            this.left_top_corner_radius = cornerRadius;
            this.left_bottom_corner_radius = cornerRadius;
            this.right_top_corner_radius = cornerRadius;
            this.right_bottom_corner_radius = cornerRadius;
            return this;
        }

        public Builder cornerRadius(float left_top_corner_radius,
                                    float left_bottom_corner_radius,
                                    float right_top_corner_radius,
                                    float right_bottom_corner_radius) {
            this.left_top_corner_radius = left_top_corner_radius;
            this.left_bottom_corner_radius = left_bottom_corner_radius;
            this.right_top_corner_radius = right_top_corner_radius;
            this.right_bottom_corner_radius = right_bottom_corner_radius;
            return this;
        }


        public Builder border(@ColorInt int border_color, float border_size) {
            this.border_color = border_color;
            this.border_size = border_size;
            return this;
        }

        public Builder progressiveRenderingEnabled(boolean progressiveRenderingEnabled) {
            this.progressiveRenderingEnabled = progressiveRenderingEnabled;
            return this;
        }

        public Builder asCircle(boolean asCircle){
            this.asCircle = asCircle;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loopTimes = loop ? -1 : 1;
            return this;
        }

        public Builder actualScaleType(ScalingUtils.ScaleType actualScaleType) {
            this.actualScaleType = actualScaleType;
            return this;
        }

        public Builder scaleWidth(int width) {
            this.scalewidth = width;
            return this;
        }

        public Builder scaleHeight(int height) {
            this.scaleheight = height;
            return this;
        }

        public Builder blurRadius(int blur_radius) {
            this.blur_radius = blur_radius;
            return this;
        }

        public Builder blurIterations(int blur_iterations) {
            this.blur_iterations = blur_iterations;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder loopTime(int looptime) {
            this.loopTimes = looptime;
            return this;
        }

        public Builder autoPlay(boolean autoPlay) {
            this.auto_play = autoPlay;
            return this;
        }

        public Builder controlListener(FrescoControllerListener frescoControllerListener) {
            this.ctlListener = frescoControllerListener;
            return this;
        }

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
