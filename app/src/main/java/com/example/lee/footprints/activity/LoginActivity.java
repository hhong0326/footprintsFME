package com.example.lee.footprints.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lee.footprints.UserProfileManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.example.lee.footprints.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener{
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 0;

    private ImageView bgImageView;
    private ImageView titleImageView;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        checkPermission();

        bgImageView = (ImageView)findViewById(R.id.bg_image_view);


        //titleImageView = findViewById(R.id.title_image_view);

        ValueAnimator animator = ValueAnimator.ofFloat(0, -1500);
        animator.addUpdateListener(this);
        animator.setDuration(20000)
                .start();

        // 로그인 Button 설정
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Firebase Authentication 객체 생성
        mAuth = FirebaseAuth.getInstance();

        // 초기 설정
        //new UserProfileManager(this).save();

    }

    private void checkPermission() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]
                                {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        3);  //마지막 인자는 체크해야될 권한 갯수

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 이전 실행 시 로그인되어 있으면 바로 MainActivity를 실행한다
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            getUserProfile(currentUser);
    }

    /* == 로그인 처리 함수들 == */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            signInButton.setVisibility(Button.INVISIBLE); //로그인 버튼 숨김
            signIn();
        }
    }

    /* 2 구글 로그인 액티비티 실행 */
    private void signIn() {
        @SuppressLint("RestrictedApi") Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.w(TAG, "구글 로그인 결과:성공");
                Toast.makeText(this, "구글 계정으로 로그인 되었습니다", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                signInButton.setVisibility(Button.VISIBLE); //로그인 버튼 다시 보임
                // [END_EXCLUDE]
            }
        }
    }


    /* 5 구글계정정보(account)로 파이어베이스 인증 (credential의 completeListener) */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Firebase Auth 구글ID로 로그인:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Firebase Auth 구글ID로 로그인 결과:성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Firebase Auth 구글ID로 로그인 결과:실패", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            signInButton.setVisibility(Button.VISIBLE); //로그인 버튼 다시 보임
                        }

                    }
                });
    }

    /* 6 사용자 프로필 (Firebase UID, 출생년도, 성별, 체중, 신장 */
    private void getUserProfile(FirebaseUser user) {
        UserProfileManager manager = new UserProfileManager(this);
        Intent intent;
        if (!user.getUid().equals(manager.getId())) {
            // 새로운 프로필을 작성하는 Activity 실행
            intent = new Intent(this, UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            // MainActivity 실행
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (float) valueAnimator.getAnimatedValue();
        bgImageView.setTranslationX(value);
        //titleImageView.setAlpha(-value / 700);
    }
}
/*
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lee.footprints.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(mClickListener);
        checkPermission();
    }
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]
                                {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                        3);  //마지막 인자는 체크해야될 권한 갯수

            }
        }
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
*/