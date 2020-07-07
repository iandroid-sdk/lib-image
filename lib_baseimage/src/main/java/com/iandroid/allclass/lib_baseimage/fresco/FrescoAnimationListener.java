package com.iandroid.allclass.lib_baseimage.fresco;
import android.util.Log;

import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.fresco.animation.drawable.AnimationListener;


public class FrescoAnimationListener implements AnimationListener {
    @Override
    public void onAnimationStart(AnimatedDrawable2 animatedDrawable2) {
        Log.d("FrescoAnimationListener", "onAnimationStart");
    }

    @Override
    public void onAnimationStop(AnimatedDrawable2 animatedDrawable2) {
        Log.d("FrescoAnimationListener", "onAnimationStop");

    }

    @Override
    public void onAnimationReset(AnimatedDrawable2 animatedDrawable2) {
        Log.d("FrescoAnimationListener", "onAnimationReset");

    }

    @Override
    public void onAnimationRepeat(AnimatedDrawable2 animatedDrawable2) {
        Log.d("FrescoAnimationListener", "onAnimationRepeat");

    }

    @Override
    public void onAnimationFrame(AnimatedDrawable2 animatedDrawable2, int i) {
        Log.d("FrescoAnimationListener", "onAnimationFrame");

    }
}
