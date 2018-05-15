package com.example.lee.footprints.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lee.footprints.R;
import com.example.lee.footprints.fragment.MapFragment;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.EditText;

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

public class AddActivity extends AppCompatActivity {

    final int REQUEST_IMAGE=100;
    ImageView imageView;
    EditText editText;
    Button button;
    private Uri fileUri;
    private String filePath;
    private UploadFile uploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        uploadFile = new UploadFile(this);

        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        editText = (EditText)findViewById(R.id.editText);

        button = (Button)findViewById(R.id.addButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile.execute(filePath);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    fileUri = data.getData();
                    filePath = getPath(fileUri);
                    Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
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
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null){
            int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_idx);
        }

        return uri.getPath();
    }
    public class UploadFile extends AsyncTask<String, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/upload";

        private ProgressDialog dialog;
        Context mContext;

        public UploadFile(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("업로드중입니다.");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.show();
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

                builder.addTextBody("name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                //builder.addTextBody("user_account", "aaa@bbb.ccc");
                builder.addTextBody("message", editText.getText().toString());

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
                dialog.dismiss();
                imageView.setImageResource(0);
                editText.setText("");
            }catch (Exception e){

            }
        }
    }


/*

    public void uploadFile(){

        HttpClient httpClient = null;

        try{
            httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httpPost = new HttpPost(url_Address);
            httpPost.setHeader("Content-Disposition", "form-data");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addTextBody("name", "");
            builder.addTextBody("message", "");

            File file = new File(filePath);
            builder.addBinaryBody("file", file);

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

    }
*/

}
