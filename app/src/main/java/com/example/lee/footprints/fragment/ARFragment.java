package com.example.lee.footprints.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lee.footprints.R;
import com.google.android.gms.maps.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.location.Address;
import android.location.Geocoder;
import android.view.TextureView;
import android.widget.ImageView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.Locale;

public class ARFragment extends Fragment implements TextureView.SurfaceTextureListener, View.OnClickListener{

    TextureView textureView;
    ImageView imageView;

    //add~~~~~~~~~~~~~
    Camera camera;
    List<Camera.Size> supportedPreviewSizes;
    Camera.Size previewSize;

    double latitude, longitude, altitude;
    long time;
    Geocoder geoCoder;
    LocationManager manager;
    View layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);

        }else {
            layout = inflater.inflate(R.layout.activity_ar, null);

            textureView = (TextureView) layout.findViewById(R.id.lab1_textureview);
            imageView = (ImageView) layout.findViewById(R.id.lab1_btn);

            textureView.setSurfaceTextureListener(this);
            imageView.setOnClickListener(this);


        }
        geoCoder = new Geocoder(getActivity(), Locale.KOREAN);
        // Inflate the layout for this fragment
        return layout;
    }

    private  void checkPermission(){
        Log.i("ASD" , "체크 퍼미션~~~");
        int permissionCheck1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET);
        if (permissionCheck1 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, 1);

        int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        int permissionCheck3 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck3 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {

        checkPermission();

        // get manager instance
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // set listener
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime, minDistance, gpsListener);
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

//        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, gpsListener);
//        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        time = location.getTime();



        //Log.i("ASD", "last:   " + latitude+ "," + longitude);

        Toast.makeText(getActivity().getApplicationContext(), "Location Service started.\nyou can test using DDMS.", Toast.LENGTH_SHORT).show();
    }




    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            time = location.getTime();
            Log.i("ASD", "onLocationChanged  " + latitude+ "," + longitude);

            StringBuffer juso = new StringBuffer();

            try {
                // 위도,경도를 이용하여 현재 위치의 주소를 가져온다.
                List<Address> addresses;
                addresses = geoCoder.getFromLocation(latitude, longitude, 1);
                for (Address addr : addresses) {
                    int index = addr.getMaxAddressLineIndex();
                    for (int i = 0; i <= index; i++) {
                        juso.append(addr.getAddressLine(i));
                        juso.append(" ");
                    }
                    juso.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getActivity().getApplicationContext(), String.valueOf(juso), Toast.LENGTH_SHORT)
                    .show();

            manager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "onProviderDisabled", Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(getActivity())
                    .setTitle("경고")
                    .setMessage("GPS가 꺼져있습니다.\n ‘위치 서비스’에서 ‘Google 위치 서비스’를 체크해주세요")
                    .setCancelable(false)   // Back Button 동작 안하도록 설정

                    // "계속" 버튼
                    .setPositiveButton("설정",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                            Toast.makeText(getActivity(), "계속 하세요~", Toast.LENGTH_SHORT).show();

                        }
                    })

                    // "종료" 버튼
                    .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "종료 버튼 눌렀네요", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getActivity(), "onStatusChanged", Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200 && grantResults.length>0) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                getActivity().setContentView(R.layout.activity_ar);

                textureView = (TextureView)getActivity().findViewById(R.id.lab1_textureview);
                textureView.setSurfaceTextureListener(this);

                imageView=(ImageView)getActivity().findViewById(R.id.lab1_btn);
                imageView.setOnClickListener(this);
            }
        }
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //add~~~~~~~~~~~~~~~~~~~~~~~~~
        camera=Camera.open();

        Camera.Parameters parameters=camera.getParameters();
        supportedPreviewSizes=parameters.getSupportedPreviewSizes();
        if(supportedPreviewSizes != null){
            previewSize= com.example.lee.footprints.camera.CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, width, height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }
        int result= com.example.lee.footprints.camera.CameraUtil.setCameraDisplayOrientation(getActivity(), 0);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setRotation(result);

        Log.i("ASD", "스타트로케이션 스타트!");
        startLocationService();

        Log.i("ASD", "저장할 위치들: " + latitude+ " " + longitude);
        parameters.setGpsLatitude(latitude);
        parameters.setGpsLongitude(longitude);
        parameters.setGpsTimestamp(time);
        parameters.setGpsAltitude(altitude);

        camera.setDisplayOrientation(result);



        camera.setParameters(parameters);
        try{
            camera.setPreviewTexture(surface);
        }catch (Exception e){
        }

        camera.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //add~~~~~~~~~~~~~~~~~~
        camera.stopPreview();
        camera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Update your view here!
    }


    @Override
    public void onClick(View v) {
        //add~~~~~~~~~~~~~~~~~~~~~~~~
        Log.i("ASD", "버튼 클릭");
        if(camera != null){
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    FileOutputStream fos;
                    try{
                        File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp");
                        Log.i("ASD", "파일0 생성");
                        Log.i("ASD", "위치: " + latitude +" "+ longitude);
                        if(!dir.exists()){
                            Log.i("ASD", "폴더 생성");
                            dir.mkdir();
                        }
                        File file=File.createTempFile("IMG-",".jpg", dir);
                        if(!file.exists()){
                            Log.i("ASD", "파일 생성");
                            file.createNewFile();
                        }
                        fos=new FileOutputStream(file);
                        Log.i("ASD", "~111111111111~~: "+ data.toString());

                        fos.write(data);
                        fos.flush();
                        fos.close();
                    }catch (Exception e){
                        Log.i("ASD", "오류~~~");
                        e.printStackTrace();
                    }
                    camera.startPreview();
                }
            });
        }
    }

}