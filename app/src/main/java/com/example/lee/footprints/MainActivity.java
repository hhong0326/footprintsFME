package com.example.lee.footprints;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by choi on 2018-03-26.
 */

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private int RESULT_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissionCamera();
        initViewPager();
    }
    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        RESULT_PERMISSIONS);

            }
        }

        return true;
    }


    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.view_pager);

        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(new TimelineFragment());
        listFragments.add(new MapFragment());
        listFragments.add(new ARFragment());
        Adapter fragmentPagerAdapter = new Adapter(getSupportFragmentManager(),listFragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"));
        tabLayout.addTab(tabLayout.newTab().setText("지도"));
        tabLayout.addTab(tabLayout.newTab().setText("카메라"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                    case 1:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                    case 2:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
}
