package com.example.lee.footprints.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.lee.footprints.fragment.MapFragment;
import com.example.lee.footprints.R;
import com.example.lee.footprints.fragment.SettingFragment;
import com.example.lee.footprints.fragment.TimelineFragment;
import com.example.lee.footprints.adapter.Adapter;
import com.example.lee.footprints.fragment.ARFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by choi on 2018-03-26.
 */

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Adapter fragmentPagerAdapter;
   // private int RESULT_PERMISSIONS = 100;

    LocationManager manager;

    com.example.lee.footprints.Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViewPager();

        startLocationService();

    }
    @SuppressLint("MissingPermission")
    private void startLocationService() {

        checkPermission();

        // get manager instance
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // set listener
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime, minDistance, gpsListener);
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

//        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, gpsListener);
//        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Log.i("LAT",Double.toString(location.getLatitude()));

        currentLocation = currentLocation.getInstance();
        currentLocation.setLocation(location.getLatitude(), location.getLongitude());

        //Log.i("ASD", "last:   " + latitude+ "," + longitude);

        Toast.makeText(getApplicationContext(), "Location Service started.\nyou can test using DDMS.", Toast.LENGTH_SHORT).show();
    }




    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            currentLocation.setLocation(location.getLatitude(), location.getLongitude());

            manager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "onProviderDisabled", Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("경고")
                    .setMessage("GPS가 꺼져있습니다.\n ‘위치 서비스’에서 ‘Google 위치 서비스’를 체크해주세요")
                    .setCancelable(false)   // Back Button 동작 안하도록 설정

                    // "계속" 버튼
                    .setPositiveButton("설정",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "계속 하세요~", Toast.LENGTH_SHORT).show();

                        }
                    })

                    // "종료" 버튼
                    .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "종료 버튼 눌렀네요", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getApplicationContext(), "onStatusChanged", Toast.LENGTH_SHORT).show();
        }

    }
    private  void checkPermission(){
        Log.i("ASD" , "체크 퍼미션~~~");
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permissionCheck1 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck3 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        fragmentPagerAdapter = new Adapter(getSupportFragmentManager(),listFragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"));
        tabLayout.addTab(tabLayout.newTab().setText("지도"));
        tabLayout.addTab(tabLayout.newTab().setText("카메라"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(2);
        viewPager.setOffscreenPageLimit(3);
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
