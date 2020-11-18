package com.iandroid.allclass.lib_baseimage.fresco;

import android.graphics.Bitmap;

import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * created by wangkm
 * on 2020/11/18.
 */
class RedMeshPostprocessor extends BasePostprocessor {
    @Override
    public String getName() {
        return "redMeshPostprocessor";
    }

    @Override
    public void process(Bitmap bitmap) {
        int width = bitmap.getWidth();         //获取位图的宽
        int height = bitmap.getHeight();       //获取位图的高
        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

    }
}
