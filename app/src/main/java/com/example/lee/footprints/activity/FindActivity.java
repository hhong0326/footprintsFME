package com.example.lee.footprints.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lee.footprints.R;
import com.example.lee.footprints.adapter.ListAdapter;
import com.example.lee.footprints.model.Tag;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class FindActivity extends AppCompatActivity {

    private EditText editText;
    private ListView listView;
    private TextView textView;
    private LinearLayout layout;
    private ArrayList<Tag> pictures;
    private ListAdapter adapter;
    private String text;
    private String[] tagArray;
    private int[] tagcountArray;

    FindPic findPic;

    @Override
    protected void onResume() {
        super.onResume();
        if(text != null) {
            /* 친구 목록에서 있나 출력 */
            //f_list_update();
            /* 추천 목록 */
            //r_list_update();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        editText = (EditText)findViewById(R.id.find_edit);
        textView = (TextView)findViewById(R.id.find_text);
        listView = (ListView)findViewById(R.id.find_list);
        /*f_list = (ListView)findViewById(R.id.find_f_list);
        r_list = (ListView)findViewById(R.id.find_r_list);
        find_f = (TextView)findViewById(R.id.find_f);
        find_r = (TextView)findViewById(R.id.find_r);*/
        layout = (LinearLayout)findViewById(R.id.find_boss);
        /*find_f.setAlpha(0.0f);
        find_r.setAlpha(0.0f);*/

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                text = s.toString();
                tagArray = null;
                tagcountArray = null;
                findPic = null;
                pictures.clear();
                findPic = new FindPic(getApplicationContext());
                findPic.execute();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                Intent intent = new Intent(FindActivity.this, FindPicActivity.class);
                                                intent.putExtra("PIC_ID",((TextView)view.findViewById(R.id.tag_name)).getText().toString().substring(1));
                                                startActivity(intent);
                                            }
                                        });
        pictures = new ArrayList<Tag>();
        adapter = new ListAdapter(this,pictures,R.layout.item);
        listView.setAdapter(adapter);
/*
        friends = new ArrayList<Friend>();
        f_adapter = new TagAdapter(this, friends , true);
        f_list.setAdapter(f_adapter);
        recommand = new ArrayList<Friend>();
        r_adapter = new TagAdapter(this, recommand, false);
        r_list.setAdapter(r_adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getDisplayName();
*/
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드 닫기
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드 닫기
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
            }
        });
/*
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = s.toString();
                /* 친구 목록에서 있나 출력 */
                //f_list_update();
                /* 추천 목록 */
                //r_list_update();/*
            //}
           // @Override
            //public void afterTextChanged(Editable s) { }
       /// });
    }
    public class FindPic extends AsyncTask<String, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/tagsearch";

        private ProgressDialog dialog;
        Context mContext;

        public FindPic(Context context){
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
                builder.addTextBody("keyword", text);

                //File file = new File(psth[0]);
                //builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);

                //서버에서 받은 response 값 저장
                String body = EntityUtils.toString(response.getEntity());
                JSONArray jsonArray = new JSONArray(body);
                StringBuffer sb = new StringBuffer();

                tagArray = new String[jsonArray.length()];
                tagcountArray = new int[jsonArray.length()];

                //데이터 뽑는 부분
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String tag = jsonObject.getString("tag");
                    int tagcount = jsonObject.getInt("int_tagCount");

                    tagArray[i] = tag;
                    tagcountArray[i] = tagcount;

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
            if(tagArray!=null && tagArray.length>0)
                for(int i=0;i<tagArray.length;i++) {
                    Log.e("TAG", tagArray[i] + tagcountArray[i]);
                    pictures.add(new Tag(tagArray[i], tagcountArray[i]));
                }
        }
    }
    /*
    public class RequestJson extends AsyncTask<Void, Integer, Void> {

        public static final int DOWNLOAD_PROGRESS = 0;

        private final String url_Address = "http://footprints.gonetis.com:8080/moo/tagsearch";

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

                tagArray = new String[jsonArray.length()];
                tagcountArray = new int[jsonArray.length()];

                //데이터 뽑는 부분
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String tag = jsonObject.getString("tag");
                    int tagcount = jsonObject.getInt("int_tagcount");

                    tagArray[i] = tag;
                    tagcountArray[i] = tagcount;

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
            if(tagArray!=null && tagArray.length>0)
                for(int i=0;i<tagArray.length;i++)
                    pictures.add(new Tag(tagArray[i],tagcountArray[i]));
        }

    }

/*
    public void f_list_update(){
        find_f.setAlpha(0.0f);
        f_adapter.removeAllItem();
        f_adapter.notifyDataSetChanged();

        Query query = FirebaseDatabase.getInstance().getReference().child("friend").child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ASDF1", "새로 시작 합니다.");
                if (dataSnapshot.getValue() == null) {
                    find_f.setAlpha(0.0f);
                    Log.d("ASDF", "친구가 없음");
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Friend friend = child.getValue(Friend.class);
                        Log.d("ASDF", "id: " + friend.getId() + " text: " + text);
                        if (friend.getId().contains(text) && text.equals("") != true) {
                            if (f_adapter.findItem(friend)) {
                                friends.add(friend);
                                find_f.setAlpha(1.0f);
                            }
                        }
                    }
                }
                f_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void r_list_update(){
        find_r.setAlpha(0.0f);
        r_adapter.removeAllItem();
        r_adapter.notifyDataSetChanged();

        Query query2 = FirebaseDatabase.getInstance().getReference().child("friend");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    find_r.setAlpha(0.0f);
                    Log.d("ASDF", "널입니다요");
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Friend friend = new Friend(child.getKey());
                        if (friend.getId().contains(text) && text.equals("") != true) {
                            if (r_adapter.findItem(friend) && f_adapter.findItem(friend)) {
                                if(!friend.getId().equals(id))
                                    recommand.add(friend);
                                find_r.setAlpha(1.0f);
                            }
                        }
                    }
                }
                r_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }*/
}