package com.iandroid.allclass.lib_baseimage.fresco;


import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.backend.AnimationBackendDelegate;

import androidx.annotation.Nullable;

public class FrescoLoopCountBackend extends AnimationBackendDelegate {
    private int loop_count;

    public FrescoLoopCountBackend(@Nullable AnimationBackend animationBackend, int loop_count) {
        super(animationBackend);
        this.loop_count = loop_count;
    }

    @Override
    public int getLoopCount() {
        return loop_count > 0 ? loop_count : super.getLoopCount();
    }
}
