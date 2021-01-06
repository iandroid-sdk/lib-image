package com.iandroid.allclass.lib_baseimage.fresco;

import com.facebook.common.memory.MemoryTrimType;
import com.facebook.imagepipeline.cache.CountingMemoryCache;

public class FrescoCacheTrimStrategy implements  CountingMemoryCache.CacheTrimStrategy {
    private final String TAG = FrescoImageCacheStatsTracker.class.getSimpleName();

    @Override
    public double getTrimRatio(MemoryTrimType trimType) {
        //LogUtils.d(TAG, "getTrimRatio");
        return 0;
    }
}
