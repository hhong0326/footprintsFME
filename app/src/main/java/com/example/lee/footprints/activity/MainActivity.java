package com.example.lee.footprints.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.lee.footprints.fragment.MapFragment;
import com.example.lee.footprints.R;
import com.example.lee.footprints.fragment.SettingFragment;
import com.example.lee.footprints.fragment.TimelineFragment;
import com.example.lee.footprints.adapter.Adapter;
import com.example.lee.footprints.fragment.ARFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by choi on 2018-03-26.
 */

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
   // private int RESULT_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViewPager();
    }
/*
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
*/

    private void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.view_pager);

        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(new TimelineFragment());
        listFragments.add(new MapFragment());
        listFragments.add(new ARFragment());
        listFragments.add(new SettingFragment());
        Adapter fragmentPagerAdapter = new Adapter(getSupportFragmentManager(),listFragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"));
        tabLayout.addTab(tabLayout.newTab().setText("지도"));
        tabLayout.addTab(tabLayout.newTab().setText("카메라"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(2);
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
                    case 3:
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
