package com.luiz.mg.magickey.models;

public class User {

    private String mat;
    private String name;
    private String dept;

    public User() {}

    public User(String mat, String name, String dept) {
        this.mat = mat;
        this.name = name;
        this.dept = dept;
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
