package com.example.lee.footprints.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lee.footprints.MyClusterRenderer;
import com.example.lee.footprints.Picture;
import com.example.lee.footprints.R;
import com.example.lee.footprints.activity.AddActivity;
import com.example.lee.footprints.activity.FindActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.places.Places;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "googlemap_example";
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private View layout;
    private LinearLayout linear;
    private GoogleMap mMap;
    private ClusterManager<Picture> mClusterManager;
    private LocationManager locationManager;

    private RequestJson requestJson;
    private RequestImage requestImage;
    private SetImageView setImageView;

    private String[] fileUrlArray;
    private Double[] latArray;
    private Double[] lngArray;

    private int num; //총 받은 사진개수

    private double currentLat;
    private double currentLng;
    private Bitmap[] image = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            layout = null;
            requestJson = null;
            requestImage = null;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.activity_map, null);
        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        requestMyLocation();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            linear = (LinearLayout) layout.findViewById(R.id.linear);
            //paths = getPathOfAllImages();
        }

        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);

        EditText editText = (EditText)layout.findViewById(R.id.find_edit);

        editText.setCursorVisible(false);
        editText.setShowSoftInputOnFocus(false);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ASD", "검색 ㄱㄱㄱㄱ");
                Intent intent = new Intent(getContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        ImageView imageView = (ImageView)layout.findViewById(R.id.board_comment);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ADD","추가");
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        requestJson = new RequestJson(getActivity());
        requestImage = new RequestImage(getActivity());
        //백그라운드 작업 실행
        requestJson.execute();

        // Inflate the layout for this fragment
        return layout;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //ACCESS_COARSE_LOCATION 권한
        if(requestCode==1){
            //권한받음
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                requestMyLocation();
            }
            //권한못받음
            else{
                Toast.makeText(getActivity(), "권한없음", Toast.LENGTH_SHORT).show();

            }
        }
    }
    //나의 위치 요청
    public void requestMyLocation(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,10,locationListener);
    }
    //위치정보 구하기 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            Log.e("lat",Double.toString(currentLat));
            Log.e("lng",Double.toString(currentLng));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLng),16));
            //나의 위치를 한번만 가져오기 위해
            locationManager.removeUpdates(locationListener);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("gps", "onStatusChanged"); }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if ( googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if ( googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if ( googleApiClient != null && googleApiClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
        if ( googleApiClient != null ) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if ( googleApiClient.isConnected()) {
                //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //나침반이 나타나도록 설정
        mMap.getUiSettings().setCompassEnabled(true);
        // 매끄럽게 이동함
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // 클러스터 매니저 생성
        mClusterManager = new ClusterManager<>(getActivity(),mMap);

        final CameraPosition[] mPreviousCameraPosition = {null};
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition position = mMap.getCameraPosition();
                mPreviousCameraPosition[0] = mMap.getCameraPosition();
                mClusterManager.cluster();
            }

        });

        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new MyClusterRenderer(getActivity(), mMap, mClusterManager));
        //한 개의 마커 누를 때!!
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Picture>() {
            @Override
            public boolean onClusterItemClick(Picture picture) {
                cleanImageView();
                setImageView = new SetImageView(picture.getImage());
                setImageView.execute();
                return false;
            }
        });

        //합쳐진(클러스터링된) 마커 누를 때!!
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Picture>() {
            @Override
            public boolean onClusterClick(Cluster<Picture> cluster) {
                cleanImageView();
                Iterator<Picture> itr = cluster.getItems().iterator();
                while(itr.hasNext()) {
                    Picture pic = itr.next();
                    setImageView = new SetImageView(pic.getImage());
                    setImageView.execute();
                }

                return false;
            }
        });

        if ( googleApiClient == null) {
            buildGoogleApiClient();
        }
        if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
    }

    // 임의의 좌표 투입
    private void addItem(Double lat, Double lng, Bitmap image) {
        String latString = String.format("%.6f", lat);
        String lngString = String.format("%.6f", lng);
        mClusterManager.addItem(new Picture(Double.parseDouble(latString), Double.parseDouble(lngString), image));
    }
/*
    public void setImageView(Bitmap image) {

        ImageView imageView = new ImageView(getActivity());

        linear.addView(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);

        imageView.setImageBitmap(image);
        Log.e("IMAGES","IMAGEVIEWS ADDED");
    }
*/
    public void cleanImageView() {
        linear.removeAllViews();
        setImageView = null;
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        googleApiClient.connect();
    }
    /*
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*
        if ( !checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            }
        } else {

            mMap.getUiSettings().setCompassEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
*/
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
/*
    // 모든 사진파일 경로 가져와서 ArrayList에 저장 + 앱 구동시 모든 경로 Log 출력
    private ArrayList<String> getPathOfAllImages()
    {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };

        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        int lastIndex;
        while (cursor.moveToNext())
        {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;

            if (!TextUtils.isEmpty(absolutePathOfImage))
            {
                result.add(absolutePathOfImage);
            }
        }

        for (String string : result)
        {
            Log.i("getPathOfAllImages", "|" + string + "|");
        }
        picnum = result.size();

        return result;
    }
*/
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
                JSONArray jsonArray = new JSONArray(body);
                StringBuffer sb = new StringBuffer();

                fileUrlArray = new String[jsonArray.length()];
                latArray = new Double[jsonArray.length()];
                lngArray = new Double[jsonArray.length()];

                num = jsonArray.length();

                //데이터 뽑는 부분
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String fileName = jsonObject.getString("thumbPicName");
                    Double latitude = jsonObject.getDouble("latitude");
                    Double longitude = jsonObject.getDouble("longitude");

                    fileUrlArray[i] = fileName;
                    latArray[i] = latitude;
                    lngArray[i] = longitude;

                    //테스트용으로 출력할 문장 저장
                    sb.append(
                            "파일명 : " + fileName + "\n위도 : " + latitude + "\n경도 : " + longitude + "\n\n"
                    );
                    //textView.setText(sb.toString());
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
            requestImage.execute();
        }

    }
    public class RequestImage extends AsyncTask <Void, Integer, Void> {

        Context mContext;
        String url_Address = "http://footprints.gonetis.com:8080/moo/resources/";
        String thumb_url_Address = "http://footprints.gonetis.com:8080/moo/resources/thumbnails/";

        public RequestImage(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (image != null)
                    for (int i = 0; i < image.length; i++)
                        image[i].recycle();
                image = null;
                image = new Bitmap[num];
                for (int i = 0; i < num; i++) {
                    image[i] = BitmapFactory.decodeStream((InputStream) new URL(thumb_url_Address + fileUrlArray[i]).getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {
            for (int i = 0; i < num; i++) {
                //if(getDistance(latArray[i],lngArray[i],currentLat,currentLng)<=1000) //meter 단위
                addItem(latArray[i], lngArray[i], image[i]);
            }
            Log.e("addItem", "gogogogogo");
        }
    }
    public class SetImageView extends AsyncTask<Void, Integer, Void> {

        Bitmap mImage;

        public SetImageView(Bitmap image) {
            mImage = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageView imageView = new ImageView(getActivity());

            linear.addView(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);

            imageView.setImageBitmap(mImage);
            Log.e("IMAGES", "IMAGEVIEWS ADDED");
        }
    }
/* 현재좌표와 마커와의 거리를 여기서 계산해야 하나??
    public double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        double distance = 0;
        Location locationA = new Location("pictureLocation");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);
        Location locationB = new Location("currentLocation");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);
        distance = locationA.distanceTo(locationB);

        return distance;
    }
*/

}
