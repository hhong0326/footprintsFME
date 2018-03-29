package com.example.lee.footprints;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.camera2.CameraDevice;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ARFragment extends Fragment{

    private CameraDevice camera;
    private SurfaceView mCameraView;
    private SurfaceHolder mCameraHolder;
    private Camera mCamera;
    private Button mStart;
    private boolean recording = false;
    private MediaRecorder mediaRecorder;

    public ARFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_ar, container, false);

        mCameraView = (SurfaceView)v.findViewById(R.id.surfaceView);

        init();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_ar, container, false);
    }
    private void init(){

        Log.d("INIT","INIT");

        // surfaceview setting
        mCameraHolder = mCameraView.getHolder();
        //mCameraHolder.addCallback(surfaceListener);
        mCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraView.setVisibility(View.VISIBLE);
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(mCameraHolder);
        }catch (IOException e) {}
        mCamera.startPreview();

    }
    private SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            mCamera.release();

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("CAMERACREATED","CREATED");
            // TODO Auto-generated method stub
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);

            try {
                if(mCamera==null) {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height);
            mCamera.startPreview();

        }
    };
/*
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // View 가 존재하지 않을 때
        if (mCameraHolder.getSurface() == null) {
            return;
        }

        // 작업을 위해 잠시 멈춘다
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // 에러가 나더라도 무시한다.
        }

        // 카메라 설정을 다시 한다.
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(parameters);

        // View 를 재생성한다.
        try {
            mCamera.setPreviewDisplay(mCameraHolder);
            mCamera.startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

*/
}
