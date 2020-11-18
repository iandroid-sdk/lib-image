package com.iandroid.allclass.lib_baseimage.fresco;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.iandroid.allclass.lib_baseimage.FrescoConfig;
import com.iandroid.allclass.lib_baseimage.IImageLoader;
import com.iandroid.allclass.lib_baseimage.ImageOptions;
import com.iandroid.allclass.lib_baseimage.Utils.DeviceUtil;
import com.rohitarya.fresco.facedetection.processor.core.FrescoFaceDetector;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class FrescoLoader extends IImageLoader {
    private static String TAG = "FrescoLoader";
    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 20 * ByteConstants.MB;
    private static final int MAX_DISK_CACHE_LOW_SIZE = 60 * ByteConstants.MB;
    private static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;
    private static final String IMAGE_PIPELINE_CACHE_DIR = "ImagePipeLine";
    private ImagePipelineConfig mConfig;

    private HashMap<String, Set<String>> loadinglist;

    private static final float MAX_IMAGE_RATIO = 3f;
    private static final float MIN_IMAGE_RATIO = 0.2f;

    @Override
    public void init(Context context, FrescoConfig frescoConfig) {
        Log.d(TAG, "-----init start-----");
        if (context == null)
            return;

        // 就是这段代码，用于清理缓存
        NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(15, 5, TimeUnit.MINUTES))
                .build();

        PoolFactory poolFactory = new PoolFactory(PoolConfig.newBuilder().build());
        ImagePipelineConfig.Builder builder = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient)
                .setBitmapMemoryCacheTrimStrategy(new FrescoCacheTrimStrategy())
                /**
                 * 必须和ImageRequest的ResizeOptions一起使用，
                 * 作用就是在图片解码时根据ResizeOptions所设的宽高的像素进行解码，
                 * 这样解码出来可以得到一个更小的Bitmap。
                 * ResizeOptions和DownsampleEnabled参数都不影响原图片的大小，
                 * 影响的是EncodeImage的大小，进而影响Decode出来的Bitmap的大小，
                 * ResizeOptions须和此参数结合使用是因为单独使用ResizeOptions的话只支持JPEG图，
                 * 所以需支持png、jpg、webp需要先设置此参数。
                 */
                .setDownsampleEnabled(true)
                /**
                 * 最终影响的是mDownsampleEnabledForNetwork参数。
                 * 这个参数的作用是在mDownsampleEnabled为true的情况下，
                 * 设置是否当这次请求是从网络中加载图片时，
                 * 来对三级缓存中的编码图片重新改变大小。
                 */
                .setResizeAndRotateEnabledForNetwork(true)
                .setPoolFactory(poolFactory)
                /**
                 * 缓存的统计数据追踪器。它是一个接口，提供了各个缓存中图片Hit与Miss的回调方法，
                 * 通常可以使用它来统计缓存命中率
                 */
                .setImageCacheStatsTracker(new FrescoImageCacheStatsTracker())

                /**
                 * 注册一个内存调节器，它将根据不同的MemoryTrimType回收类型在需要降低内存使用时
                 * 候进行回收一些内存缓存资源(Bitmap和Encode)。数值越大，表示要回收的资源越多。
                 */
                .setMemoryTrimmableRegistry(NoOpMemoryTrimmableRegistry.getInstance())
                /**
                 * 执行各个任务的线程池配置，包括配置执行IO任务、后台任务、优先级低的后台任务、Decode任务的线程池的配置。
                 */
                .setExecutorSupplier(new FrescoExecutorSupplier(poolFactory.getFlexByteArrayPoolMaxNumThreads()));

        Supplier<File> baseDirectoryPathSupplier = () -> {
            File cache = null;
            try {
                if (frescoConfig != null && !TextUtils.isEmpty(frescoConfig.getLocalDir()))
                    cache = new File(frescoConfig.getLocalDir());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (cache == null
                    && context != null) {
                cache = context.getApplicationContext().getCacheDir();
            }
            return cache;
        };

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPathSupplier(baseDirectoryPathSupplier)
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)
                .build();
        builder.setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapMemoryCacheParamsSupplier(new LangBitmapMemoryCacheParamsSupplier((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)))
                .setDiskCacheEnabled(true)
                .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY);

        mConfig = builder.build();

        Fresco.initialize(context, mConfig);
        FrescoFaceDetector.initialize(context);
        FLog.setMinimumLoggingLevel(FLog.WARN);
        Log.d(TAG, "-----init end-----");
    }


    @Override
    public void clearMemoryCaches() {
        try {
            ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
            Log.d(TAG, "-----clearMemoryCaches-----");
        } catch (Exception e) {

        }
    }

    @Override
    public void displayImage(View imageView, String url, ImageOptions imageOptions) {
        if (imageView == null)
            return;

        if (!(imageView instanceof SimpleDraweeView))
            return;

        Uri uri = null;
        if (!TextUtils.isEmpty(url)
                && !url.toLowerCase().startsWith("http")) {
            url = "file://" + url;
        }
        displayImage((SimpleDraweeView) imageView, uri, url, imageOptions);
    }

    @Override
    public void displayImage(SimpleDraweeView imageView, Uri uri, ImageOptions imageOptions) {
        super.displayImage(imageView, uri, imageOptions);
        displayImage(imageView, uri, null, imageOptions);
    }

    private void displayImage(SimpleDraweeView imageView, Uri uri, String pathUrl, ImageOptions imageOptions) {
        if (uri == null)
            uri = Uri.parse(TextUtils.isEmpty(pathUrl) ? "" : pathUrl);

        String tag = null;
        boolean auto_play = true;

        PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        pipelineDraweeControllerBuilder.setCallerContext(imageView.getContext());
        pipelineDraweeControllerBuilder.setOldController(imageView.getController());

        ImageRequestBuilder imageRequestBuilder = null;
        boolean progressiveRenderingEnabled = false;

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();

        if (layoutParams != null) {
            if (imageOptions != null) {
                if (imageOptions.getWidth() != 0) {
                    layoutParams.width = imageOptions.getWidth();
                }

                if (imageOptions.getHeight() != 0) {
                    layoutParams.height = imageOptions.getHeight();
                }
                if (imageOptions.getHeight() != 0 || imageOptions.getWidth() != 0) {
                    imageView.setLayoutParams(layoutParams);
                }
            }
            if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
                    || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                if (imageOptions == null)
                    imageOptions = new ImageOptions.Builder().build();
                if (imageOptions.getCtlListener() == null)
                    imageOptions.setCtlListener(new FrescoControllerListener());
            }
        }
        if (imageOptions != null) {
            if (imageOptions.getAspectRatio() > 0) {
                imageView.setAspectRatio(imageOptions.getAspectRatio());
            }

            progressiveRenderingEnabled = imageOptions.isProgressiveRenderingEnabled();

            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            if (hierarchy != null) {
                //设置展位图
                if (imageOptions.getPlaceholderImage() != 0) {
                    hierarchy.setPlaceholderImage(imageOptions.getPlaceholderImage(), imageOptions.getPlaceholderScaleType());
                }

                //设置异常图
                if (imageOptions.getFailureImage() != 0) {
                    hierarchy.setFailureImage(imageOptions.getFailureImage(), imageOptions.getFailureScaleType());
                }

                //设置加载图的裁剪方式
                if (imageOptions.getActualScaleType() != null) {
                    hierarchy.setActualImageScaleType(imageOptions.getActualScaleType());
                }

                RoundingParams roundingParams = hierarchy.getRoundingParams();
                //圆圈
                if (imageOptions.isAsCircle()) {
                    if (roundingParams == null)
                        roundingParams = RoundingParams.asCircle();
                    roundingParams.setRoundAsCircle(true);
                }

                //圆角
                if (imageOptions.isHasCorver()) {
                    if (roundingParams == null)
                        roundingParams = new RoundingParams();
                    roundingParams.setCornersRadii(
                            imageOptions.getLeft_top_corner_radius(),
                            imageOptions.getRight_top_corner_radius(),
                            imageOptions.getRight_bottom_corner_radius(),
                            imageOptions.getLeft_bottom_corner_radius());
                }

                //边框
                if (imageOptions.getBorder_color() != 0
                        && imageOptions.getBorder_size() > 0) {
                    if (roundingParams == null)
                        roundingParams = new RoundingParams();

                    roundingParams.setBorder(imageOptions.getBorder_color(), imageOptions.getBorder_size());
                }

                if (roundingParams != null && imageOptions.getOverlayColor() != 0) {
                    roundingParams.setOverlayColor(imageOptions.getOverlayColor());
                }

                if (roundingParams != null)
                    hierarchy.setRoundingParams(roundingParams);

                imageView.setHierarchy(hierarchy);
            }

            //处理模糊度
            final int radius = imageOptions.getBlur_radius();
            if (radius > 0) {
                //对于模糊的图片，强制缩放处理
                if (imageOptions.getScalewidth() == 0)
                    imageOptions.setScalewidth(DeviceUtil.dip2px(imageView.getContext(), 100));
                if (imageOptions.getScaleheight() == 0)
                    imageOptions.setScaleheight(DeviceUtil.dip2px(imageView.getContext(), 100));

                imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);


                if (imageOptions.getBlur_iterations() > 0) {
                    imageRequestBuilder.setPostprocessor(new IterativeBoxBlurPostProcessor(imageOptions.getBlur_iterations(), radius));
                } else {
                    imageRequestBuilder.setPostprocessor(new IterativeBoxBlurPostProcessor(radius));
                }
            }
            //处理resize
            ResizeOptions resizeOptions = imageOptions.getResizeOptions();
            if (resizeOptions != null) {
                Log.d(TAG, "resize, pathurl:" + pathUrl);
                if (imageRequestBuilder == null)
                    imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
                imageRequestBuilder.setResizeOptions(resizeOptions);
            }

            if (imageOptions != null && imageOptions.isNeedBlackWhite()) {
                imageOptions.setBasePostprocessor(new RedMeshPostprocessor());
            }

            if (imageOptions != null && imageOptions.getBasePostprocessor() != null) {
                if (imageRequestBuilder == null)
                    imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);

                imageRequestBuilder.setPostprocessor(imageOptions.getBasePostprocessor());
            }

            //拿到tag
            tag = imageOptions.getTag();
            //是否自动播放动画
            auto_play = imageOptions.isAuto_play();

            if (imageOptions.getLoopTimes() > 0) {
                if (imageOptions.getCtlListener() == null)
                    imageOptions.setCtlListener(new FrescoControllerListener());
                imageOptions.getCtlListener().setLoopTimes(imageOptions.getLoopTimes());
            }

            //设置controller监听
            if (imageOptions.getCtlListener() != null) {
                if (imageOptions.getAspectRatio() < 0)
                    imageOptions.getCtlListener().setAspectRatio(imageOptions.getAspectRatio());
                imageOptions.getCtlListener().setAutoPlay(auto_play);
                pipelineDraweeControllerBuilder.setControllerListener(imageOptions.getCtlListener());
            }
        }

        if (imageRequestBuilder != null) {

            Log.d(TAG, "setImageRequest:" + uri.toString());
            pipelineDraweeControllerBuilder.setImageRequest(imageRequestBuilder
                    .setProgressiveRenderingEnabled(progressiveRenderingEnabled)
                    .build());
        } else {
            Log.d(TAG, "setUri:" + uri.toString());
            pipelineDraweeControllerBuilder.setUri(uri);
        }

        if (imageOptions != null && imageOptions.getCtlListener() != null)
            imageOptions.getCtlListener().setImageView(imageView);

        pipelineDraweeControllerBuilder.setAutoPlayAnimations(auto_play);
        imageView.setController(pipelineDraweeControllerBuilder.build());

        String uriPath = TextUtils.isEmpty(pathUrl) ? "" : pathUrl;
        if (!TextUtils.isEmpty(tag)
                && !TextUtils.isEmpty(uriPath)) {
            if (loadinglist == null)
                loadinglist = new HashMap<>();
            Set<String> uriList;
            if (loadinglist.containsKey(tag)) {
                uriList = loadinglist.get(tag);
            } else {
                uriList = new HashSet<>();
                loadinglist.put(tag, uriList);
            }

            if (uriList != null
                    && !uriList.contains(uriPath)) {
                uriList.add(uriPath);
                Log.d(TAG, "add item-->tag:" + tag + ",img:" + uriPath);
            }
        }

        Log.d(TAG, "load-->tag:" + tag + ",img:" + pathUrl);
    }

    @Override
    public void displayImage(View view, int resID, ImageOptions imageOptions) {
        if (view == null
                || resID == 0)
            return;
        if (view instanceof SimpleDraweeView) {
            Uri uri = new Uri.Builder().scheme("res")
                    .authority(view.getContext().getPackageName())
                    .appendPath(String.valueOf(resID))
                    .build();
            displayImage((SimpleDraweeView) view, uri, null, imageOptions);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(resID);
        }
    }

    @Override
    public void clearMemoryCacheByUri(Uri uri) {
        super.clearMemoryCacheByUri(uri);
        if (uri == null)
            return;

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (imagePipeline != null) {
            imagePipeline.evictFromMemoryCache(uri);
            Log.d(TAG, "clear By Uri-->img:" + uri.getPath());
        }
    }

    @Override
    public void clearMemoryCacheByTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }

        Log.d(TAG, "-----clear By tag(" + tag + ") Start-----");
        if (loadinglist != null
                && loadinglist.containsKey(tag)) {
            Set<String> uriList = loadinglist.get(tag);
            if (uriList != null
                    && uriList.size() > 0) {
                for (String path : uriList) {
                    if (!TextUtils.isEmpty(path))
                        clearMemoryCacheByUri(Uri.parse(path));
                }
                uriList.clear();
            }
            loadinglist.remove(tag);
        }
        Log.d(TAG, "-----clear By tag(" + tag + ") End-----");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void fastblur(Bitmap sentBitmap, int radius, float scale) {
        int w = sentBitmap.getWidth();
        int h = sentBitmap.getHeight();
        int[] pix;

        if (scale > 0) {
            Bitmap smallBitmap = null;
            try {
                smallBitmap = Bitmap.createScaledBitmap(sentBitmap, (int) (w * scale), (int) (h * scale), false);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            if (radius < 1 || smallBitmap == null) {
                return;
            }
            sentBitmap.copy(smallBitmap.getConfig(), false);
            sentBitmap.setHeight(smallBitmap.getHeight());
            sentBitmap.setWidth(smallBitmap.getWidth());
            w = smallBitmap.getWidth();
            h = smallBitmap.getHeight();
            pix = new int[w * h];
            smallBitmap.getPixels(pix, 0, w, 0, 0, w, h);
        } else {
            pix = new int[w * h];
            sentBitmap.getPixels(pix, 0, w, 0, 0, w, h);
        }

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        sentBitmap.setPixels(pix, 0, w, 0, 0, w, h);
    }
}
