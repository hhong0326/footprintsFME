package com.example.lee.footprints;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private static Location singletonInstance;

    private Double latitude;
    private Double longitude;

    public static Location getInstance() {
        if(singletonInstance==null){
            singletonInstance = new Location();
        }
        return singletonInstance;
    }

    private Location() {
    }

    public LatLng getLocation() {
        return new LatLng(singletonInstance.latitude, singletonInstance.longitude);
    }

    public void setLocation(Double lat, Double lng) {
        singletonInstance.latitude = lat;
        singletonInstance.longitude = lng;
    }

}