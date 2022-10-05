package com.luiz.mg.magickey.models;

public class Entry {

    private String nameKey;

    private String matUserTakeKey;
    private String nameUserTakeKey;
    private String dateTimeTakeKey;

    private String matUserBackKey;
    private String nameUserBackKey;
    private String dateTimeBackKey;

    public Entry() {
    }

    public Entry(String nameKey, String matUserTakeKey, String nameUserTakeKey,
                 String dateTimeTakeKey, String matUserBackKey, String nameUserBackKey,
                 String dateTimeBackKey) {
        this.nameKey = nameKey;
        this.matUserTakeKey = matUserTakeKey;
        this.nameUserTakeKey = nameUserTakeKey;
        this.dateTimeTakeKey = dateTimeTakeKey;
        this.matUserBackKey = matUserBackKey;
        this.nameUserBackKey = nameUserBackKey;
        this.dateTimeBackKey = dateTimeBackKey;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getMatUserTakeKey() {
        return matUserTakeKey;
    }

    public void setMatUserTakeKey(String matUserTakeKey) {
        this.matUserTakeKey = matUserTakeKey;
    }

    public String getNameUserTakeKey() {
        return nameUserTakeKey;
    }

    public void setNameUserTakeKey(String nameUserTakeKey) {
        this.nameUserTakeKey = nameUserTakeKey;
    }

    public String getDateTimeTakeKey() {
        return dateTimeTakeKey;
    }

    public void setDateTimeTakeKey(String dateTimeTakeKey) {
        this.dateTimeTakeKey = dateTimeTakeKey;
    }

    public String getMatUserBackKey() {
        return matUserBackKey;
    }

    public void setMatUserBackKey(String matUserBackKey) {
        this.matUserBackKey = matUserBackKey;
    }

    public String getNameUserBackKey() {
        return nameUserBackKey;
    }

    public void setNameUserBackKey(String nameUserBackKey) {
        this.nameUserBackKey = nameUserBackKey;
    }

    public String getDateTimeBackKey() {
        return dateTimeBackKey;
    }

    public void setDateTimeBackKey(String dateTimeBackKey) {
        this.dateTimeBackKey = dateTimeBackKey;
    }
}
