package com.example.lee.footprints;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/* 단일 마커 하나하나 객체 */

public class Picture implements ClusterItem {
    private Double latitude;
    private Double longitude;
    private Bitmap image;
    private String fileURL;
    private LatLng location;
    private String useraccount;
    private String username;
    private String tags;

    public Picture(Double latitude, Double longitude, Bitmap image, String fileURL, String useraccount, String username, String tags) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.fileURL = fileURL;
        this.useraccount = useraccount;
        this.username = username;
        this.tags = tags;

        location = new LatLng(this.latitude,this.longitude);
    }

    public Bitmap getImage() {
        return image;
    }
    public String getURL() { return fileURL; }
    public String getUseraccount() { return useraccount; }
    public String getUsername() { return username; }
    public String getTags() { return tags; }
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
