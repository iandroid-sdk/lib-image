package com.iandroid.allclass.lib_baseimage.fresco;

import android.app.ActivityManager;
import android.os.Build;

import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;

/**
 * Created by jianyanfu on 22/02/2018.
 */

public class LangBitmapMemoryCacheParamsSupplier implements Supplier<MemoryCacheParams> {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    //使用的缓存数量
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

    private static final int MAX_CACHE_ENTRIES = 300;
    private static final int MAX_EVICTION_QUEUE_SIZE = 100 * ByteConstants.MB;
    private static final int MAX_EVICTION_QUEUE_ENTRIES = 300;
    private static final int MAX_CACHE_ENTRY_SIZE = Integer.MAX_VALUE;

    private final ActivityManager mActivityManager;

    public LangBitmapMemoryCacheParamsSupplier(ActivityManager activityManager) {
        mActivityManager = activityManager;
    }

    @Override
    public MemoryCacheParams get() {
        return new MemoryCacheParams(
                getMaxCacheSize(),
                MAX_CACHE_ENTRIES,
                MAX_EVICTION_QUEUE_SIZE,
                MAX_EVICTION_QUEUE_ENTRIES,
                MAX_CACHE_ENTRY_SIZE);
    }

    private int getMaxCacheSize() {
        final int maxMemory =
                Math.min(mActivityManager.getMemoryClass() * ByteConstants.MB, Integer.MAX_VALUE);
        if (maxMemory < 32 * ByteConstants.MB) {
            return 4 * ByteConstants.MB;
        } else if (maxMemory < 64 * ByteConstants.MB) {
            return 6 * ByteConstants.MB;
        } else {
            // We don't want to use more ashmem on Gingerbread for now, since it doesn't respond well to
            // native memory pressure (doesn't throw exceptions, crashes app, crashes phone)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                return 8 * ByteConstants.MB;
            } else {
                return maxMemory / 4;
            }
        }
    }
}
