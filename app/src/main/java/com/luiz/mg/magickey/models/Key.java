package com.luiz.mg.magickey.models;

public class Key {

    private String name;
    private String dept;
    private boolean borr;
    private String matBorr;

    public Key() {}

    public Key(String name, String dept, boolean borrowed, String matBorr) {
        this.name = name;
        this.dept = dept;
        this.borr = borrowed;
        this.matBorr = matBorr;
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

    public boolean getBorr() {
        return borr;
    }

    public void setBorr(boolean borrowed) {
        this.borr = borrowed;
    }

    public String getMatBorr() {
        return matBorr;
    }

    public void setMatBorr(String matBorr) {
        this.matBorr = matBorr;
    }
}
