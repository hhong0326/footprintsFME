package com.example.lee.footprints.model;

public class Tag {
    private String id;
    private int num;

    public Tag(){}

    public Tag(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}