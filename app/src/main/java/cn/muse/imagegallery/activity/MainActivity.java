package cn.muse.imagegallery.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import cn.muse.imagegallery.R;

public class MainActivity extends AppCompatActivity {

    private final String[] urlArray = {"http://img.lanrentuku.com/img/allimg/1707/14988864745279.jpg",
            "http://img.lanrentuku.com/img/allimg/1609/147479906785.jpg",
            "http://img.lanrentuku.com/img/allimg/1609/14747974667766.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, ImageShowActivity.class);
        intent.putStringArrayListExtra("imgPathList", new ArrayList<>(Arrays.asList(urlArray)));
        intent.putExtra("selectIndex", 1);
        startActivity(intent);
    }
}
