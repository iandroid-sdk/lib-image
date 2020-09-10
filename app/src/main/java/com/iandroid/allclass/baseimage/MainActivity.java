package com.iandroid.allclass.baseimage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.iandroid.allclass.lib_baseimage.FrescoConfig;
import com.iandroid.allclass.lib_baseimage.ImageLoader;

public class MainActivity extends AppCompatActivity {
    public static final String FOLDER_FRESCO_CACHE = "Fresco";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoader.init(this, new FrescoConfig.Builder()
                .localDir("")
                .build());
    }
}
