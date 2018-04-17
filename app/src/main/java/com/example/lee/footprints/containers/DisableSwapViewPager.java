package com.example.lee.footprints.containers;
/**
 * Created by youngjae on 2018-04-03.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 사용처: UserProfileActivity
 * 역할  : 첫 로그인 시, 사용자 프로필(생일, 체중, 성별 등)을
 * 입력받는 프래그먼트를 담는 Container
 */
public class DisableSwapViewPager extends ViewPager {

    public DisableSwapViewPager(Context context) {
        super(context);
    }

    public DisableSwapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }
}
