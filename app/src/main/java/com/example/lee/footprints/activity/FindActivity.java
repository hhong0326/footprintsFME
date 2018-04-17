package com.example.lee.footprints.activity;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lee.footprints.R;
import com.example.lee.footprints.adapter.TagAdapter;

import java.util.ArrayList;


public class FindActivity extends AppCompatActivity {

    private EditText editText;
    private ListView f_list, r_list;
    private TextView textView, find_f, find_r;
    private LinearLayout layout;
    private ArrayList<Tag> friends, recommand;
    private TagAdapter f_adapter, r_adapter;
    private String text ,id;
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
        f_list = (ListView)findViewById(R.id.find_f_list);
        r_list = (ListView)findViewById(R.id.find_r_list);
        find_f = (TextView)findViewById(R.id.find_f);
        find_r = (TextView)findViewById(R.id.find_r);
        layout = (LinearLayout)findViewById(R.id.find_boss);
        find_f.setAlpha(0.0f);
        find_r.setAlpha(0.0f);
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = s.toString();
                /* 친구 목록에서 있나 출력 */
                //f_list_update();
                /* 추천 목록 */
                //r_list_update();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
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