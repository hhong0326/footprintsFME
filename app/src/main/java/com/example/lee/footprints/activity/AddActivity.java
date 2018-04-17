package com.example.lee.footprints.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lee.footprints.R;
import android.widget.EditText;
public class AddActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 앨범 여는 코드 + 사진 가져오는 코드 + imageView에 비트맵 출력하는 코드
            }
        });

        editText = (EditText)findViewById(R.id.editText);

        button = (Button)findViewById(R.id.addButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 서버에 업로드 하는 코드
            }
        });
    }

}