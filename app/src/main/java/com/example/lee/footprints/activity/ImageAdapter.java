package com.example.lee.footprints.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lee.footprints.Picture;
import com.example.lee.footprints.R;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

public class ImageAdapter extends PagerAdapter {
    Context context;
    Vector<Picture> picCollection;
    RequestImage requestImage;

    Bitmap image;
    String url_Address = "http://footprints.gonetis.com:8080/moo/resources/";

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
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

             ImageView imageView = new ImageView(context);

            imageView.setPadding(5, 5, 5, 5);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        imageView.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f));
        TextView nickname = new TextView(context);
        nickname.setText(picCollection.get(position).getUsername());
        nickname.setTextColor(Color.WHITE);
        nickname.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView email = new TextView(context);
        email.setText(picCollection.get(position).getUseraccount());
        email.setTextColor(Color.WHITE);
        email.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tags = new TextView(context);
        tags.setText(picCollection.get(position).getTags());
        tags.setTextColor(Color.WHITE);
        tags.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        Log.e("????????", picCollection.get(position).getUsername()+picCollection.get(position).getUseraccount()+picCollection.get(position).getTags());
            //requestImage = new RequestImage(context, position);
            //requestImage.execute();
            //imageView.setImageBitmap(image[position]);

            Log.e("POSITION", Integer.toString(position));
            /*
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }



            imageView.setImageBitmap(image);
            */

        Glide.with(context)
                .load(url_Address + picCollection.get(position).getURL()).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading_error).fitCenter()
                .into(imageView);

        Log.e("GLIDE",Double.toString(imageView.getWidth())+","+Double.toString(imageView.getHeight()));


        Log.e("URL",url_Address + picCollection.get(position).getURL());
            Log.e("set", "셋");
        layout.addView(imageView);
        layout.addView(nickname);
        layout.addView(email);
        layout.addView(tags);

        ((ViewPager) container).addView(layout);
            return layout;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    public class RequestImage extends AsyncTask<Void, Integer, Void> {

        Context mContext;
        int mPosition;
        String url_Address = "http://footprints.gonetis.com:8080/moo/resources/";

        public RequestImage(Context context, int position){
            mContext = context;
            mPosition = position;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){

            try{
                image = BitmapFactory.decodeStream((InputStream) new URL(url_Address + picCollection.get(mPosition).getURL()).getContent());

                Log.e("SAVE","저장"+mPosition);
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result){

        }

    }

}