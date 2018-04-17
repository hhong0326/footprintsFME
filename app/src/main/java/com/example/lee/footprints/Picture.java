package com.example.lee.footprints;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/* 단일 마커 하나하나 객체 */

public class Picture implements ClusterItem {
    private Double latitude;
    private Double longitude;
    private Bitmap image;

    private LatLng location;

    public Picture(Double latitude, Double longitude, Bitmap image) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;

        location = new LatLng(this.latitude,this.longitude);
    }

    public Bitmap getImage() {
        return image;
    }
    /*public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getPath() {
        return picPath;
    }
*/
    @Override
    public LatLng getPosition() {
        return location;
    }
}
