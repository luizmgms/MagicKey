package com.luiz.mg.magickey.models;

public class Key {

    private String name;
    private String dept;
    private boolean borr;
    private String matBorr;
    private String nameBorr;

    public Key() {}

    public Key(String name, String dept, boolean borrowed, String matBorr, String nameBorr) {
        this.name = name;
        this.dept = dept;
        this.borr = borrowed;
        this.matBorr = matBorr;
        this.nameBorr = nameBorr;
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

    public String getNameBorr() {
        return nameBorr;
    }

    public void setNameBorr(String nameBorr) {
        this.nameBorr = nameBorr;
    }
}
