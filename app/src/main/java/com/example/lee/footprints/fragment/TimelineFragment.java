package com.example.lee.footprints.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.lee.footprints.activity.ImageActivity;
import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Box;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Sphere;
import com.viro.core.Text;
import com.viro.core.Vector;
import com.viro.core.ViroMediaRecorder;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;



import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import com.example.lee.footprints.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    private static final String TAG = TimelineFragment.class.getSimpleName();
    protected ViroView mViroView;
    private View mHudGroupView;
    private Button mCameraButton;
    private Button mCalculateButton;

    private Text footPrintsText;
    private AssetManager mAssetManager;

    private TextView tv;
    private ToggleButton tb;

    private Text macDoText;
    private Text testText;
    private Box findBox;
    private Box testBox;

    Node boxNode;
    Node boxNode2;

    private int count=0;
    private float a;
    private float b;
    private float c;



    private PowerManager.WakeLock mWakeLock;	// 화면이 점멸되지 않게 하기 위함

    //센서 위치 정보
    // 센서와 위치 정보를 읽기 위함
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav, sensorMag;
    //private LocationManager locationMgr;
    private boolean isGpsEnabled;
    private Sensor orientationSensor;
    private SensorManager sensorMgr_ori;

    private double deviceLat;
    private double deviceLon;

    //status
    private enum TRACK_STATUS{
        FINDING_SURFACE,
        SURFACE_NOT_FOUND,
        SURFACE_FOUND,
    }

    private TRACK_STATUS mStatus = TRACK_STATUS.SURFACE_NOT_FOUND;

    private Context context;


    //server
    private RequestJson requestJson;
    JSONArray jsonArray;

    private String[] fileUrlArray;
    private double[] latArray;
    private double[] lngArray;
    private String[] useraccountArray;
    private String[] usernameArray;
    private String[] thumbArray;
    private String[] tagArray;


    //서버에서 받은 위치사진 데이터
    Box box[];
    Node node[];


    private int num; //총 받은 사진개수

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestJson = new RequestJson(getActivity());

        requestJson.execute();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            //화면에 실제로 보일때
        }
        else
        {
            //preload 될때(전페이지에 있을때)
            context = null;
            requestJson = null;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();



        mViroView = new ViroViewARCore(context, new ViroViewARCore.StartupListener() {
            @Override
            public void onSuccess() {
                // Override this function to start building your scene here! We provide a sample
                // "Hello World" scene


                if(latArray.length!=0)
                    createARHelloWorldScene();
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError error, String errorMessage) {
                // Fail as you wish!
                Log.d("uh", errorMessage);
            }
        });

        //container
        View view = inflater.inflate(R.layout.activity_timeline, mViroView);


        //setContentView(mViroView);

        //4월 10일 주석처리
        //View.inflate(context, R.layout.activity_arr, ((ViewGroup) mViroView));
        mHudGroupView = view.findViewById(R.id.main_layout);
        mHudGroupView.setVisibility(View.GONE);

        //위치관리자 설정
        final LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 위치관리자 객체를 얻어온다

        tv = (TextView) view.findViewById(R.id.gps_value);
        tv.setText("위치정보 미수신중");

        tb = (ToggleButton)view.findViewById(R.id.toggle1);


        if (!hasRecordingStoragePermissions(getActivity().getBaseContext())){
            requestPermissions();
            //return;
        }

//        //위치 정보 얻기
//        try{
//                tv.setText("수신중..");
//                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
//                locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
//                        100, // 통지사이의 최소 시간간격 (miliSecond)
//                        1, // 통지사이의 최소 변경거리 (m)
//                        mLocationListener);
//                locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
//                        100, // 통지사이의 최소 시간간격 (miliSecond)
//                        1, // 통지사이의 최소 변경거리 (m)
//                        mLocationListener);
//
//        }catch(SecurityException ex){
//        }

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tb.isChecked()){
                        tv.setText("수신중..");
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                1000, // 통지사이의 최소 시간간격 (miliSecond)
                                10, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                1000, // 통지사이의 최소 시간간격 (miliSecond)
                                10, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                    }else{
                        tv.setText("위치정보 미수신중");
                        locationMgr.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                    }
                }catch(SecurityException ex){
                }
            }
        });



        return view;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            deviceLat = location.getLatitude();
            deviceLon = location.getLongitude();
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            tv.setText("위치정보 : " + provider + "\n위도 : " + latitude + "\n경도 : " + longitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

            if(count==0) {
                //boxNode.setPosition(convLocationToMerc(37.582613, 127.009867));
                count++;
            }

        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
//    public static TimelineFragment newInstance() {
//
//        Bundle args = new Bundle();
//
//        TimelineFragment fragment = new TimelineFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    private void createARHelloWorldScene() {
        final ARScene arScene = new ARScene();
        Node rootNode = arScene.getRootNode();
        boxNode = new Node();
        boxNode2 = new Node();
        node = new Node[num];
        box = new Box[num];

        for(int i=0; i<num; i++){
            node[i] = new Node();
            box[i] = new Box(10, 10, 10);
        }


        // Create a Text geometry with the words "Hello World!"
        footPrintsText = new Text.TextBuilder().viroContext(mViroView.getViroContext()).
                textString("Initializing...").
                fontFamilyName("NotoSansCJK").fontSize(40).
                color(Color.GREEN).
                width(3).height(2).
                horizontalAlignment(Text.HorizontalAlignment.CENTER).
                verticalAlignment(Text.VerticalAlignment.CENTER).
                lineBreakMode(Text.LineBreakMode.NONE).
                clipMode(Text.ClipMode.CLIP_TO_BOUNDS).
                maxLines(1).build();

        //test
        testText = new Text.TextBuilder().viroContext(mViroView.getViroContext()).
                textString("Initializing...").
                fontFamilyName("NotoSansCJK").fontSize(100).
                color(Color.BLACK).
                width(3).height(2).
                horizontalAlignment(Text.HorizontalAlignment.CENTER).
                verticalAlignment(Text.VerticalAlignment.CENTER).
                lineBreakMode(Text.LineBreakMode.NONE).
                clipMode(Text.ClipMode.CLIP_TO_BOUNDS).
                maxLines(1).build();

        //박스
        // Create a Text geometry with the words "Hello World!"
//        macDoText = new Text.TextBuilder().viroContext(mViroView.getViroContext()).
//                textString("Initializing...").
//                fontFamilyName("NotoSansCJK").fontSize(60).
//                color(Color.YELLOW).
//                width(5).height(4).
//                horizontalAlignment(Text.HorizontalAlignment.CENTER).
//                verticalAlignment(Text.VerticalAlignment.CENTER).
//                lineBreakMode(Text.LineBreakMode.NONE).
//                clipMode(Text.ClipMode.CLIP_TO_BOUNDS).
//                maxLines(1).build();

        // Create a Node, position it, and attach the Text geometry to it
        Node textNode = new Node();
        Node testNode = new Node();
        Node macDoNode = new Node();

        findBox = new Box(3, 10, 1);

        testBox = new Box(10, 10, 10);
        boxNode.setGeometry(findBox);

        //37.557928, 127.196087
        boxNode.setPosition(convLocationToVec(37.557928, 127.196087));

        boxNode2.setGeometry(testBox);
        boxNode2.setPosition(convLocationToVec(37.581577, 127.009833));

        //37.582583, 127.009767


        //boxNode.setPosition(new Vector(1.4138674, -0.5, -1));
        // 사진의 위치값을 벡터 값으로 바꾸어 text의 포지션을 설정한다! ****
        //37.561589, 127.190382


        //37.591220, 127.219557
        //macDoNode.setPosition(convLocationToVec(37.561589, 127.219557));

//        macDoNode.setPosition(convLocationToMerc(37.591220, 127.190386));
//        macDoNode.setScale(new Vector(3,3,3));
//        macDoNode.setGeometry(macDoText);


        //37.561608, 127.190548
        testNode.setPosition(new Vector(1.4138547, -0.5, -3));
        testNode.setScale(new Vector(.3, .3, .3));
        testNode.setGeometry(testText);
        testNode.setRotationPivot(new Vector(0, 0.2, -1));

        //발자취
        textNode.setPosition(new Vector(0, 0, -1));
        //textNode.setPosition(convLocationToMerc(37.561602, 127.190486));
        textNode.setScale(new Vector(.3, .3, .3));
        textNode.setGeometry(footPrintsText);


        //server box 서버 연동! 박스!
        for(int i=0; i<num; i++){
            Log.d("ㅎㅎ", "ㅎㅎ"+ num);

            node[i].setGeometry(box[i]);
            node[i].setPosition(convLocationToVec(0, 0));
            node[i].setClickListener(new ClickListener() {
                @Override
                public void onClick(int i, Node node, Vector vector) {

                }

                @Override
                public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                    onBoxClick(clickState, box[i]);
                }
            });

            rootNode.addChildNode(node[i]);
        }

        Toast ttoast = Toast.makeText(context, "으아! " + latArray.length + "우오! " + latArray[1], Toast.LENGTH_SHORT);
        ttoast.show();

        //touch event
        textNode.setClickListener(new ClickListener() {
            @Override
            public void onClick(int i, Node node, Vector vector) {
                //helloWorldText.dispose(); // 안먹히나?
            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                //helloWorldText.setColor(Color.RED);
                onTextClick(clickState, footPrintsText);
            }
        });

        // Attach the textNode to the ARScene's root Node

        Node earth = new Node();
        earth.setPosition(new Vector(0, 0, -10));
        earth.setGeometry(new Sphere(2));

        Node moon = new Node();
        moon.setPosition(new Vector(-1.4, 3, -3));
        moon.setGeometry(new Sphere(1f));
        earth.addChildNode(moon);


        rootNode.addChildNode(textNode);
        rootNode.addChildNode(earth);

        //rootNode.addChildNode(testNode);
        //rootNode.addChildNode(macDoNode);
        rootNode.addChildNode(boxNode);
        rootNode.addChildNode(boxNode2);
        rootNode.addChildNode(testNode);

        // Create an ARScene.Listener to listen for the onTrackingInitialized() callback. This
        // callback tells us when the AR system is ready to go


        //4월 14일 주석처리
        SampleARSceneListener arSceneListener = new SampleARSceneListener(new Runnable() {
            @Override
            public void run() {
                footPrintsText.setText("발자취");
                //macDoText.setText("진도마트~");
                Log.d("홍순일", "도는거니");


            }
        });



        initARHud();

        //4월10일
//        arScene.setListener(new SampleARSceneListener(this, mViroView));
//        arScene.getRootNode().addLight(new AmbientLight(Color.WHITE, 1000f));
//        mViroView.setScene(arScene);
//        View.inflate(this, R.layout.activity_ar, ((ViewGroup) mViroView));

        //4월 14일 주석처리
        // Start our tracking UI when the scene is ready to be tracked
        arScene.setListener(arSceneListener);

        // Finally set the arScene on the renderer
        mViroView.setScene(arScene);

    }

    // You can use the ARSceneListener to respond to AR events, including the detection of
    // anchors
    private class SampleARSceneListener implements ARScene.Listener{
        private Runnable mOnTrackingInitializedRunnable;
        private WeakReference<Activity> mCurrentActivityWeak;
        private boolean mInitialized;

        //4월 10일 주석
//        public SampleARSceneListener(Activity activity, View rootView) {
//
//            //4월 10일 추가
//            mCurrentActivityWeak = new WeakReference<Activity>(activity);
//            mInitialized = false;
//        }
        public SampleARSceneListener(Runnable onTrackingInitializedRunnable) {
//          //4월 10일 주석처리
            mOnTrackingInitializedRunnable = onTrackingInitializedRunnable;
            mInitialized = false;
        }

        //          Runnable
//        @Override
//        public void run(){
//            helloWorldText.setText("발자취");
//        }
        @Override
        public void onTrackingUpdated(ARScene.TrackingState trackingState,
                                      ARScene.TrackingStateReason trackingStateReason) {
            if (trackingState == ARScene.TrackingState.NORMAL && !mInitialized) {
                mHudGroupView.setVisibility(View.VISIBLE);



                //4월 10일 추가
//                Activity activity = mCurrentActivityWeak.get();
//                if(activity == null){
//                    return;
//                }
//                mCameraButton = (Button) activity.findViewById(R.id.photo_btn);

                // Update our UI views to the finding surface state.
                //4월 10일 주석처리
                //setTrackingStatus(TRACK_STATUS.FINDING_SURFACE);

                mInitialized = true;

                //4월 10일 주석처리
                if (mOnTrackingInitializedRunnable != null) {
                    mOnTrackingInitializedRunnable.run();

                }
            }
        }

        @Override
        public void onTrackingInitialized() {
            // this method is deprecated.
        }

        @Override
        public void onAmbientLightUpdate(float lightIntensity, float colorTemperature) {
            // no-op
        }

        @Override
        public void onAnchorFound(ARAnchor anchor, ARNode arNode) {
            // no-op
            // Create a Box to sit on every plane we detect
            if (anchor.getType() == ARAnchor.Type.ANCHOR) {
//                Box box = new Box(1, 1, 1);
//                Node boxNode = new Node();
//                boxNode.setGeometry(box);
//                arNode.addChildNode(boxNode);

            }
        }

        @Override
        public void onAnchorUpdated(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onAnchorRemoved(ARAnchor anchor, ARNode arNode) {
            // no-op
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mViroView.onActivityStarted(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mViroView.onActivityResumed(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mViroView.onActivityPaused(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        mViroView.onActivityStopped(getActivity());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mViroView.onActivityDestroyed(getActivity());
    }

    //권한 설정
    private void requestPermissions(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
    }

    private static boolean hasRecordingStoragePermissions(Context context) {
        boolean hasExternalStoragePerm = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return hasExternalStoragePerm;
    }
    private static boolean hasLocationPermissions(Context context) {
        boolean hasLocationPerm = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return hasLocationPerm;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (!hasRecordingStoragePermissions(context)) {
            Toast toast = Toast.makeText(context, "User denied external storage permissions", Toast.LENGTH_LONG);
            toast.show();
        }
        if (!hasLocationPermissions(context)) {
            Toast toast = Toast.makeText(context, "User denied location permissions", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void initARHud(){

        // Bind the back button on the top left of the layout
//        ImageView view = (ImageView) findViewById(R.id.ar_back_button);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProductARActivity.this.finish();
//            }
//        });

        // Bind the detail buttons on the top right of the layout.
//        ImageView productDetails = (ImageView) findViewById(R.id.ar_details_page);
//        productDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProductARActivity.this, ProductDetailActivity.class);
//                intent.putExtra(INTENT_PRODUCT_KEY, mSelectedProduct.mName);
//                startActivity(intent);
//            }
//        });

        mViroView.findViewById(R.id.bottom_frame_controls).setVisibility(View.VISIBLE);

        //Calculate the location
        mCalculateButton = (Button) mViroView.findViewById(R.id.calc_btn);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count+=0.01;

                //boxNode.setPosition(convLocationToVec(37.557928, 127.196087));
                //boxNode2.setPosition(convLocationToVec(37.581577, 127.009833));

                for(int i=0; i<jsonArray.length(); i++){
                    node[i].setPosition(convLocationToVec(latArray[i], lngArray[i]));
//                    Toast toast = Toast.makeText(context, "gg" + latArray[i], Toast.LENGTH_LONG);
//                    toast.show();
                }


            }
        });
        // Bind the camera button on the bottom, for taking images.
        mCameraButton  = (Button) mViroView.findViewById(R.id.photo_btn);
        final File photoFile = new File(context.getFilesDir(), "screenShot");
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //권한 불러오기
                if (!hasRecordingStoragePermissions(getActivity().getBaseContext())){
                    requestPermissions();
                    return;
                }

                Toast toast = Toast.makeText(context, "버튼클릭!", Toast.LENGTH_SHORT);
                toast.show();

                mViroView.getRecorder().takeScreenShotAsync("screenShot", true, new ViroMediaRecorder.ScreenshotFinishListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap, String s) {
                        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/png");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));
                        startActivity(Intent.createChooser(shareIntent, "Share image using"));

                    }

                    @Override
                    public void onError(ViroMediaRecorder.Error error) {
                        Log.e("Viro","onTaskFailed " + error.toString());
                    }
                });
            }
        });

    }

    private void onTextClick(ClickState state, Text t){
//        if (state == ClickState.CLICK_DOWN){
//            t.setColor(Color.RED);
//        } else if (state == ClickState.CLICK_UP) {
//           t.setColor(Color.GREEN);
//        }
        if(state == ClickState.CLICKED){
            if(t.getColor() == Color.GREEN){
                t.setColor(Color.RED);
            }
            else{
                t.setColor(Color.GREEN);
            }
        }
    }

    private void onBoxClick(ClickState state, Box t){
//        if (state == ClickState.CLICK_DOWN){
//            t.setColor(Color.RED);
//        } else if (state == ClickState.CLICK_UP) {
//           t.setColor(Color.GREEN);
//        }
        if(state == ClickState.CLICKED){
            Intent intent = new Intent(getActivity(), ImageActivity.class);
            intent.putExtra("PIC_NUM","");
            startActivity(intent);
        }
        else{

        }
    }

    public Vector convLocationToVec(double lati, double longi){

        Vector v = new Vector();
        Vector vD = convDeviceLocationToVec(deviceLat, deviceLon);

        double cosLati = Math.cos(lati*Math.PI/180.0);
        double sinLati = Math.sin(lati*Math.PI/180.0);
        double cosLongi = Math.cos(longi*Math.PI/180.0);
        double sinLongi = Math.sin(longi*Math.PI/180.0);
        double radian = 6378137.0;

        double f = 1.0 / 298.257224;
        double C = 1.0 / Math.sqrt(cosLati * cosLati + (1 - f) * (1 - f) * sinLati * sinLati);
        double S = (1.0 - f) * (1.0 - f) * C;
        double h = 0.0;

        v.x = (float) ((radian * C + h) * cosLati * cosLongi);
        v.y = (float) ((radian * C + h) * cosLati * sinLongi);
        v.z = (float) ((radian * S + h) * sinLati);

        v.x = v.x - vD.x;
        v.z = v.y- vD.y;
        v.y = 1;

        a = v.x;
        b = v.y;
        c = v.z;

//        Toast ttoast = Toast.makeText(ArActivity.this, "x : "+ v.x + "y :" + v.y + "z :" + v.z, Toast.LENGTH_SHORT);
//        ttoast.show();

        return v;
    }

    public Vector convDeviceLocationToVec(double lati, double longi){

        Vector v = new Vector();
        double cosLati = Math.cos(lati*Math.PI/180.0);
        double sinLati = Math.sin(lati*Math.PI/180.0);
        double cosLongi = Math.cos(longi*Math.PI/180.0);
        double sinLongi = Math.sin(longi*Math.PI/180.0);
        double radian = 6378137.0;

        double f = 1.0 / 298.257224;
        double C = 1.0 / Math.sqrt(cosLati * cosLati + (1 - f) * (1 - f) * sinLati * sinLati);
        double S = (1.0 - f) * (1.0 - f) * C;
        double h = 0.0;

        v.x = (float) ((radian * C + h) * cosLati * cosLongi);
        v.y = (float) ((radian * C + h) * cosLati * sinLongi);
        v.z = (float) ((radian * S + h) * sinLati);

        a = v.x;
        b = v.y;
        c = v.z;

//        Toast ttoast = Toast.makeText(TimelineFragment.this, "x : "+ v.x + "y :" + v.y + "z :" + v.z, Toast.LENGTH_SHORT);
//        ttoast.show();

        return v;
    }



//    private void setTrackingStatus(TRACK_STATUS status) {
//        if (mStatus == TRACK_STATUS.SELECTED_SURFACE || mStatus == status){
//            return;
//        }
//
//        // If the surface has been selected, we no longer need our cross hair listener.
//        if (status == TRACK_STATUS.SELECTED_SURFACE){
//            ((ViroViewARCore)mViroView).setCameraARHitTestListener(null);
//        }
//
//        mStatus = status;
//
//    }

    public class RequestJson extends AsyncTask<Void, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/jsonrequest";

        private ProgressDialog dialog;
        Context mContext;

        public RequestJson(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            HttpClient httpClient = null;

            try{

                httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httpPost = new HttpPost(url_Address);

                HttpResponse response = httpClient.execute(httpPost);

                //서버에서 받은 response 값 저장
                String body = EntityUtils.toString(response.getEntity());
                jsonArray = new JSONArray(body);
                StringBuffer sb = new StringBuffer();

                fileUrlArray = new String[jsonArray.length()];
                latArray = new double[jsonArray.length()];
                lngArray = new double[jsonArray.length()];
                useraccountArray = new String[jsonArray.length()];
                usernameArray = new String[jsonArray.length()];
                thumbArray = new String[jsonArray.length()];
                tagArray = new String[jsonArray.length()];

                num = jsonArray.length();

                //데이터 뽑는 부분
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String userAccount = jsonObject.getString("user_account");
                    String userName = jsonObject.getString("username");
                    String thumbName = jsonObject.getString("thumbPicName");
                    String fileName = jsonObject.getString("fileName");
                    Double latitude = jsonObject.getDouble("latitude");
                    Double longitude = jsonObject.getDouble("longitude");
                    String tags = jsonObject.getString("tags");

                    fileUrlArray[i] = fileName;
                    latArray[i] = latitude;
                    lngArray[i] = longitude;
                    useraccountArray[i] = userAccount;
                    usernameArray[i] = userName;
                    thumbArray[i] = thumbName;
                    tagArray[i] = tags;

                    //테스트용으로 출력할 문장 저장
                    sb.append(
                            "파일명 : " + fileName + "\n위도 : " + latitude + "\n경도 : " + longitude + "\n\n"
                    );

                    //textView.setText(sb.toString());
                    Log.d("홍", "ㅎ"+ latArray[i]);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(httpClient != null){
                    httpClient.getConnectionManager().shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result){
            //requestImage.execute();
        }

    }
}
