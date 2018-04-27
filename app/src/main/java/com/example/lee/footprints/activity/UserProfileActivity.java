package com.example.lee.footprints.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.footprints.CircleImageView;
import com.example.lee.footprints.UserProfileManager;
import com.example.lee.footprints.containers.DisableSwapViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.lee.footprints.R;

/* 처음 로그인했을 때 사용자 프로필을 작성하는 Activity */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private int position;
    private UserProfileManager manager;

    private DisableSwapViewPager viewPager;
    private TextView skipBtn;
    private TextView nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user);

        manager = new UserProfileManager(this);

        UserProfileViewPagerAdapter adapter = new UserProfileViewPagerAdapter(getSupportFragmentManager());
        viewPager = (DisableSwapViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        skipBtn = (TextView)findViewById(R.id.skipBtn);
        nextBtn = (TextView)findViewById(R.id.nextBtn);
        skipBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == skipBtn) {
            startMain();
        } else if (v == nextBtn) {
            if (position < 1) {

                TextView text_id = (TextView)findViewById(R.id.text_id);
                EditText edit_id = (EditText)findViewById(R.id.profile_id);
                ImageView img_overlap = (ImageView)findViewById(R.id.profile_overlap);



                viewPager.setCurrentItem(++position);
            } else if (position < 2) {
                TextView textView = (TextView)findViewById(R.id.profile_img);
                CircleImageView imageView = (CircleImageView)findViewById(R.id.profile_pic);


                viewPager.setCurrentItem(++position);
            } else {
                startMain();
            }
        }
    }

    private void startMain() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "세션이 만료되었습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        manager.setId(user.getUid());
        manager.save();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* 뷰페이저 관련 클래스 */
    public static class UserProfileFragment extends Fragment {
        public UserProfileFragment() {
        }

        public static UserProfileFragment newInstance(int position) {
            UserProfileFragment fragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int position = getArguments().getInt("position");
            int layoutId;
            if (position == 0)
                layoutId = R.layout.fragment_user_profile_1;
            else
                layoutId = R.layout.fragment_user_profile_2;
            return inflater.inflate(layoutId, container, false);
        }
    }

    private class UserProfileViewPagerAdapter extends FragmentPagerAdapter {

        UserProfileViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return UserProfileFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}