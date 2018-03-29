package com.example.lee.footprints;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(mClickListener);
    }
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    Log.d("OnClickListener", "click loginButton");
                    // 액티비티 실행
                    Intent intentActivity =
                            new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentActivity);
                    finish();
            }
        }
    };
}
