package com.luiz.mg.magickey.models;

public class Entry {

    private String nameKey;

    private String matUserTakeKey;
    private String nameUserTakeKey;
    private String dateTakeKey;
    private String timeTakeKey;

    private String matUserBackKey;
    private String nameUserBackKey;
    private String dateBackKey;
    private String timeBackKey;

    public Entry(String nameKey, String matUserTakeKey, String nameUserTakeKey,
                 String dateTakeKey, String timeTakeKey, String matUserBackKey,
                 String nameUserBackKey, String dateBackKey, String timeBackKey) {

        this.nameKey = nameKey;
        this.matUserTakeKey = matUserTakeKey;
        this.nameUserTakeKey = nameUserTakeKey;
        this.dateTakeKey = dateTakeKey;
        this.timeTakeKey = timeTakeKey;
        this.matUserBackKey = matUserBackKey;
        this.nameUserBackKey = nameUserBackKey;
        this.dateBackKey = dateBackKey;
        this.timeBackKey = timeBackKey;
    }

    public String getName() {
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

    public String getDateTakeKey() {
        return dateTakeKey;
    }

    public void setDateTakeKey(String dateTakeKey) {
        this.dateTakeKey = dateTakeKey;
    }

    public String getTimeTakeKey() {
        return timeTakeKey;
    }

    public void setTimeTakeKey(String timeTakeKey) {
        this.timeTakeKey = timeTakeKey;
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

    public String getDateBackKey() {
        return dateBackKey;
    }

    public void setDateBackKey(String dateBackKey) {
        this.dateBackKey = dateBackKey;
    }

    public String getTimeBackKey() {
        return timeBackKey;
    }

    public void setTimeBackKey(String timeBackKey) {
        this.timeBackKey = timeBackKey;
    }
}
