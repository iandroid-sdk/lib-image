package com.iandroid.allclass.baseimage;

import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.iandroid.allclass.lib_baseimage.FrescoConfig;
import com.iandroid.allclass.lib_baseimage.ImageLoader;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String FOLDER_FRESCO_CACHE = "Fresco";
    SimpleDraweeView vv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.init(this, new FrescoConfig.Builder()
                .localDir("")
                .build());
        setContentView(R.layout.activity_main);
        vv = findViewById(R.id.my_image_view);
        ImageLoader.displayImage(vv, R.drawable.ic_radio_spectrum);

    }
}
