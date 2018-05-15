package com.example.lee.footprints.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//껍데기

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.footprints.R;
import com.example.lee.footprints.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    final int REQUEST_IMAGE=100;
    private Uri fileUri;
    private String filePath;
    ImageView imageView;
    private UploadProfile uploadProfile;
    TextView email;
    EditText nickname;
    EditText intro;
    Button acceptBtn;
    Button resetBtn;
    Button logoutBtn;
    Bitmap resetProfile;
    String resetNickname;
    String resetIntro;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_setting, container, false);

        email = (TextView)layout.findViewById(R.id.email);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        nickname = (EditText)layout.findViewById(R.id.nickname);
        intro = (EditText)layout.findViewById(R.id.intro);

        uploadProfile = new UploadProfile(getActivity());

        acceptBtn = (Button)layout.findViewById(R.id.accept);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfile.execute(filePath);
            }
        });

        resetBtn = (Button)layout.findViewById(R.id.reset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //불러와서 초기화시켜야함~~
            }
        });

        logoutBtn = (Button)layout.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("FIREBASELOGOUT",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        imageView = (ImageView)layout.findViewById(R.id.profileimg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        // Inflate the layout for this fragment
        return layout;
    }

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    fileUri = data.getData();
                    filePath = getPath(fileUri);
                    Bitmap image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    imageView.setImageBitmap(rotateImage(image, 90));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // 이미지 회전 함수
    public Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),src.getHeight(), matrix, true);
    }

    public String getPath(Uri uri){
        if (uri == null){
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null){
            int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_idx);
        }

        return uri.getPath();
    }
    public class UploadProfile extends AsyncTask<String, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/profileupdate";

        private ProgressDialog dialog;
        Context mContext;

        public UploadProfile(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... psth){
            HttpClient httpClient = null;

            try{
                httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httpPost = new HttpPost(url_Address);
                httpPost.setHeader("Content-type", "multipart/form-data;boundary=-------------");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setBoundary("-------------");
                builder.setCharset(Charset.forName("UTF-8"));
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //builder.addTextBody("name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                builder.addTextBody("username", nickname.getText().toString());
                builder.addTextBody("intro", intro.getText().toString());
                builder.addTextBody("user_account", email.getText().toString());

                File file = new File(psth[0]);
                builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
            }catch (IOException e){
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
            dialog.setProgress((int)progress[0]);
        }

        @Override
        protected void onPostExecute(Void result){
            try{
                Toast.makeText(getContext(), "저장 완료", Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        }
    }
}