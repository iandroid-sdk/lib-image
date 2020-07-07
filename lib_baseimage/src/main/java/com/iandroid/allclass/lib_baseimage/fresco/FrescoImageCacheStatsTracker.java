package com.iandroid.allclass.lib_baseimage.fresco;

import android.util.Log;

import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker;
import com.facebook.imagepipeline.cache.MemoryCache;

public class FrescoImageCacheStatsTracker implements ImageCacheStatsTracker {
    private final String TAG = FrescoImageCacheStatsTracker.class.getSimpleName();

    @Override
    public void onBitmapCachePut(CacheKey cacheKey) {
        Log.d(TAG, "onBitmapCachePut");
    }

    @Override
    public void onBitmapCacheHit(CacheKey cacheKey) {
        Log.d(TAG, "onBitmapCacheHit:" + cacheKey.getUriString());
    }

    @Override
    public void onBitmapCacheMiss(CacheKey cacheKey) {
        Log.d(TAG, "onBitmapCacheMiss");
    }

    @Override
    public void onMemoryCachePut(CacheKey cacheKey) {
        Log.d(TAG, "onMemoryCachePut");
    }

    @Override
    public void onMemoryCacheHit(CacheKey cacheKey) {
        Log.d(TAG, "onMemoryCacheHit");
    }

    @Override
    public void onMemoryCacheMiss(CacheKey cacheKey) {
        Log.d(TAG, "onMemoryCacheMiss");
    }

    @Override
    public void onStagingAreaHit(CacheKey cacheKey) {
        Log.d(TAG, "onStagingAreaHit:" + cacheKey.getUriString());
    }

    @Override
    public void onStagingAreaMiss(CacheKey cacheKey) {
        Log.d(TAG, "onStagingAreaMiss");
    }

    @Override
    public void onDiskCacheHit(CacheKey cacheKey) {
        Log.d(TAG, "onDiskCacheHit" + cacheKey.getUriString());
    }

    @Override
    public void onDiskCacheMiss(CacheKey cacheKey) {
        Log.d(TAG, "onDiskCacheMiss");
    }

    @Override
    public void onDiskCacheGetFail(CacheKey cacheKey) {
        Log.d(TAG, "onDiskCacheGetFail");
    }

    @Override
    public void onDiskCachePut(CacheKey cacheKey) {

    }

    @Override
    public void registerBitmapMemoryCache(MemoryCache<?, ?> bitmapMemoryCache) {

    }

    @Override
    public void registerEncodedMemoryCache(MemoryCache<?, ?> encodedMemoryCache) {

    }
}
