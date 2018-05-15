package com.example.lee.footprints.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.lee.footprints.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class FindPicActivity extends Activity {

    FindPictures findPictures;
    String picID;
    PictureList[] pictureLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpictures);

        Intent intent = getIntent();
        picID = intent.getStringExtra("PIC_ID");

        findPictures = new FindPictures(this);
        findPictures.execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        findPictures = null;
    }
    public class PictureList {
        String user_account;
        String username;
        String filename;
        String picname_thumb;
        Double latitude;
        Double longitude;
        String tags;
    }
    public class FindPictures extends AsyncTask<String, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/searchjson";

        private ProgressDialog dialog;
        Context mContext;

        public FindPictures(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... psth) {
            HttpClient httpClient = null;

            try {
                httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httpPost = new HttpPost(url_Address);
                httpPost.setHeader("Content-type", "multipart/form-data;boundary=-------------");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setBoundary("-------------");
                builder.setCharset(Charset.forName("UTF-8"));
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //builder.addTextBody("name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                builder.addTextBody("keyword", picID);

                //File file = new File(psth[0]);
                //builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);

                //서버에서 받은 response 값 저장
                String body = EntityUtils.toString(response.getEntity());
                JSONArray jsonArray = new JSONArray(body);
                StringBuffer sb = new StringBuffer();

                pictureLists = new PictureList[jsonArray.length()];

                //데이터 뽑는 부분
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    pictureLists[i] = new PictureList();

                    pictureLists[i].user_account = jsonObject.getString("user_account");
                    pictureLists[i].username = jsonObject.getString("username");
                    pictureLists[i].filename = jsonObject.getString("fileName");
                    pictureLists[i].picname_thumb = jsonObject.getString("thumbPicName");
                    pictureLists[i].latitude = jsonObject.getDouble("latitude");
                    pictureLists[i].longitude = jsonObject.getDouble("longitude");
                    pictureLists[i].tags = jsonObject.getString("tags");

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {
            for(int i=0;i<pictureLists.length;i++) {
                Log.i("PICTURELISTS!!!!!!!!", pictureLists[i].user_account+"|"+pictureLists[i].username+"|"+pictureLists[i].filename+"|"+pictureLists[i].picname_thumb+"|"+pictureLists[i].latitude+"|"+pictureLists[i].longitude+"|"+pictureLists[i].tags);
            }
        }
    }
}