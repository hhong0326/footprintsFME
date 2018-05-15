package com.example.lee.footprints.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.lee.footprints.R;
import com.example.lee.footprints.fragment.MapFragment;

public class ImageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
                 WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
                 layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                layoutParams.dimAmount= 0.7f;
                 getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        int picNum = intent.getIntExtra("PIC_NUM",0);
        Log.e("PICNUM",Integer.toString(picNum));

        ViewPager viewPager = (ViewPager) findViewById(R.id.image_view_pager);
        ImageAdapter adapter = new ImageAdapter(this, MapFragment.picCollection);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(picNum);
        viewPager.setOffscreenPageLimit(3);


    }

}