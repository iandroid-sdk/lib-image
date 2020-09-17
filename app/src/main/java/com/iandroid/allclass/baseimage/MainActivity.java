package com.iandroid.allclass.baseimage;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String FOLDER_FRESCO_CACHE = "Fresco";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = new Uri.Builder().scheme("res")
                .authority(getPackageName())
                .appendPath(String.valueOf(R.drawable.ic_launcher_background))
                .build();
       /* ImageLoader.init(this, new FrescoConfig.Builder()
                .localDir("")
                .build());*/
    }
}
