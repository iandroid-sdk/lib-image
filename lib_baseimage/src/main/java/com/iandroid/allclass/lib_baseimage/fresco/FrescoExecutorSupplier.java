package com.iandroid.allclass.lib_baseimage.fresco;

import android.os.Process;

import com.facebook.imagepipeline.core.ExecutorSupplier;
import com.facebook.imagepipeline.core.PriorityThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * @see com.facebook.imagepipeline.core.DefaultExecutorSupplier
 */
final class FrescoExecutorSupplier implements ExecutorSupplier {
    // Allows for simultaneous reads and writes.
    private static final int NUM_IO_BOUND_THREADS = 2;
    private static final int NUM_LIGHTWEIGHT_BACKGROUND_THREADS = 1;

    private final Executor mIoBoundExecutor;
    private final Executor mDecodeExecutor;
    private final Executor mBackgroundExecutor;
    private final Executor mLightWeightBackgroundExecutor;
    private final Executor mThumbnailProducer;

    private static class CounterBackgroundThreadFactory extends PriorityThreadFactory {
        private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);
        private String suffix;

        CounterBackgroundThreadFactory(String suffix) {
            super(Process.THREAD_PRIORITY_BACKGROUND);
            this.suffix = suffix;
        }

        @Override
        public Thread newThread(@NonNull final Runnable r) {
            Thread t = super.newThread(r);
            t.setName("Fresco#" + THREAD_COUNTER.getAndIncrement() + "-" + suffix);
            return t;
        }
    }

    FrescoExecutorSupplier(int numCpuBoundThreads) {
        mIoBoundExecutor = Executors.newFixedThreadPool(
                NUM_IO_BOUND_THREADS, new CounterBackgroundThreadFactory("IO"));

        mDecodeExecutor = Executors.newFixedThreadPool(
                numCpuBoundThreads,
                new CounterBackgroundThreadFactory("Decode"));

        mBackgroundExecutor = Executors.newFixedThreadPool(
                numCpuBoundThreads,
                new CounterBackgroundThreadFactory("Back"));

        mLightWeightBackgroundExecutor = Executors.newFixedThreadPool(
                NUM_LIGHTWEIGHT_BACKGROUND_THREADS,
                new CounterBackgroundThreadFactory("LW"));

        mThumbnailProducer = Executors.newFixedThreadPool(
                numCpuBoundThreads,
                new CounterBackgroundThreadFactory("thumbnail"));
    }

    @Override
    public Executor forLocalStorageRead() {
        return mIoBoundExecutor;
    }

    @Override
    public Executor forLocalStorageWrite() {
        return mIoBoundExecutor;
    }

    @Override
    public Executor forDecode() {
        return mDecodeExecutor;
    }

    @Override
    public Executor forBackgroundTasks() {
        return mBackgroundExecutor;
    }

    @Override
    public Executor forLightweightBackgroundTasks() {
        return mLightWeightBackgroundExecutor;
    }

    @Override
    public Executor forThumbnailProducer() {
        return mThumbnailProducer;
    }
}
