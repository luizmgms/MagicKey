package com.luiz.mg.magickey.models;

public class Key {

    private String Name;
    private String Dept;
    private boolean Borr;

    public Key() {}

    public Key(String name, String dept, boolean borrowed) {
        this.Name = name;
        this.Dept = dept;
        this.Borr = borrowed;
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

    public boolean getBorr() {
        return Borr;
    }

    public void setBorr(boolean borrowed) {
        Borr = borrowed;
    }
}
