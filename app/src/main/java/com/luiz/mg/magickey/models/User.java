package com.luiz.mg.magickey.models;

public class User {

    private String Mat;
    private String Name;
    private String Dept;

    public User(String mat, String name, String dept) {
        Mat = mat;
        Name = name;
        Dept = dept;
    }

    public String getMat() {
        return Mat;
    }

    public void setMat(String mat) {
        Mat = mat;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDept() {
        return Dept;
    }

    public void setDept(String dept) {
        Dept = dept;
    }
}
