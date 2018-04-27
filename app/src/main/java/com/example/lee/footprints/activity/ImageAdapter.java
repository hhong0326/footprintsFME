package com.example.lee.footprints.activity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.lee.footprints.Picture;

import java.util.Vector;

public class ImageAdapter extends PagerAdapter {
    Context context;
    Vector<Picture> picCollection;

    ImageAdapter(Context context, Vector<Picture> picCollection){
        this.context=context;
        this.picCollection = picCollection;
    }
    @Override
    public int getCount() {
        return picCollection.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

       ImageView imageView = new ImageView(context);

        imageView.setPadding(5, 5, 5, 5);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        imageView.setImageBitmap(picCollection.get(position).getImage());

        Log.e("POSITION",Integer.toString(position));
        ((ViewPager) container).addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}