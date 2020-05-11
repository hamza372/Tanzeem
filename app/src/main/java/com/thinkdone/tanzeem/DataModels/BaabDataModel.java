package com.thinkdone.tanzeem.DataModels;

public class BaabDataModel extends KitabDataModel{
    int babId;
    String baabName;
    int bookId;

    public BaabDataModel(int babId, String baadName, int bookId) {
        this.babId = babId;
        this.baabName = baadName;
        this.bookId = bookId;
    }

    public BaabDataModel() {
    }

    public int getBabId() {
        return babId;
    }

    public void setBabId(int babId) {
        this.babId = babId;
    }

    public String getBaabName() {
        return baabName;
    }

    public void setBaabName(String baabName) {
        this.baabName = baabName;
    }

    @Override
    public int getBookId() {
        return bookId;
    }

    @Override
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
