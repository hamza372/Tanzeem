package com.thinkdone.tanzeem.DataModels;

public class CategoryDataModel {
    int categoryId;
    String categoryNameEng;
    String categoryNameUrdu;
    int sortOrder;

    public CategoryDataModel() {
    }

    public CategoryDataModel(int categoryId, String categoryNameEng, String categoryNameUrdu, int sortOrder) {
        this.categoryId = categoryId;
        this.categoryNameEng = categoryNameEng;
        this.categoryNameUrdu = categoryNameUrdu;
        this.sortOrder = sortOrder;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryNameEng() {
        return categoryNameEng;
    }

    public void setCategoryNameEng(String categoryNameEng) {
        this.categoryNameEng = categoryNameEng;
    }

    public String getCategoryNameUrdu() {
        return categoryNameUrdu;
    }

    public void setCategoryNameUrdu(String categoryNameUrdu) {
        this.categoryNameUrdu = categoryNameUrdu;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
