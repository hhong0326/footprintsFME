package com.example.lee.footprints;

/**
 * Created by youngjae on 2018-04-03.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfileManager {
    private final SharedPreferences preferences;
    private String id; //Firebase에 등록된 구글ID
    private String birth; //생년월일
    private String gender; //성별
    private float weight; //체중
    private float height; //신장

    public UserProfileManager(Context context) {
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        id = preferences.getString("id", null);
        birth = preferences.getString("birth", null);
        gender = preferences.getString("gender", null);
        weight = preferences.getFloat("weight", 0);
        height = preferences.getFloat("height", 0);
    }

    public void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.putString("birth", birth);
        editor.putString("gender", gender);
        editor.putFloat("weight", weight);
        editor.putFloat("height", height);
        editor.apply();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}