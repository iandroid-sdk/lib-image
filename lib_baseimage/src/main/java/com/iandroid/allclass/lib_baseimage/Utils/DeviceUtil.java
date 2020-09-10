package com.iandroid.allclass.lib_baseimage.Utils;

import android.content.Context;

/**
 * created by wangkm
 * on 2020/9/10.
 */
public class DeviceUtil {

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @param dp
     * @return px
     */
    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
