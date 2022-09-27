package com.luiz.mg.magickey.models;

public class Key {
    private String nameKey;
    private String deptKey;
    private String borrowedKey;

    public Key(String name, String dept, String borrowed) {
        this.nameKey = name;
        this.deptKey = dept;
        this.borrowedKey = borrowed;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String name) {
        nameKey = name;
    }

    public String getDeptKey() {
        return deptKey;
    }

    public void setDeptKey(String dept) {
        deptKey = dept;
    }

    public String getBorrowedKey() {
        return borrowedKey;
    }

    public void setBorrowedKey(String borrowed) {
        borrowedKey = borrowed;
    }
}
