package com.thinkdone.tanzeem.DataModels;

public class PageDataModel extends  BaabDataModel{
    int page_id;
    int page_no;
    String pageTitleUrdu;
    String pageDetail;
    String pageDetailWeb;
    String pageDetailWebTranslator;
    int pageBaabId;
    boolean pageIsImage;
    String pageImageUrl;
    String descUrdu;
    int cursorIndex;

    public PageDataModel() {
    }

    public PageDataModel(int page_id, int page_no, String pageTitleUrdu, String pageDetail, String pageDetailWeb, String pageDetailWebTranslator, int pageBaabId, boolean pageIsImage, String pageImageUrl) {
        this.page_id = page_id;
        this.page_no = page_no;
        this.pageTitleUrdu = pageTitleUrdu;
        this.pageDetail = pageDetail;
        this.pageDetailWeb = pageDetailWeb;
        this.pageDetailWebTranslator = pageDetailWebTranslator;
        this.pageBaabId = pageBaabId;
        this.pageIsImage = pageIsImage;
        this.pageImageUrl = pageImageUrl;
    }

    public int getCursorIndex() {
        return cursorIndex;
    }

    public void setCursorIndex(int cursorIndex) {
        this.cursorIndex = cursorIndex;
    }

    public String getDescUrdu() {
        return descUrdu;
    }

    public void setDescUrdu(String descUrdu) {
        this.descUrdu = descUrdu;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public String getPageTitleUrdu() {
        return pageTitleUrdu;
    }

    public void setPageTitleUrdu(String pageTitleUrdu) {
        this.pageTitleUrdu = pageTitleUrdu;
    }

    public String getPageDetail() {
        return pageDetail;
    }

    public void setPageDetail(String pageDetail) {
        this.pageDetail = pageDetail;
    }

    public String getPageDetailWeb() {
        return pageDetailWeb;
    }

    public void setPageDetailWeb(String pageDetailWeb) {
        this.pageDetailWeb = pageDetailWeb;
    }

    public String getPageDetailWebTranslator() {
        return pageDetailWebTranslator;
    }

    public void setPageDetailWebTranslator(String pageDetailWebTranslator) {
        this.pageDetailWebTranslator = pageDetailWebTranslator;
    }

    public int getPageBaabId() {
        return pageBaabId;
    }

    public void setPageBaabId(int pageBaabId) {
        this.pageBaabId = pageBaabId;
    }

    public boolean isPageIsImage() {
        return pageIsImage;
    }

    public void setPageIsImage(boolean pageIsImage) {
        this.pageIsImage = pageIsImage;
    }

    public String getPageImageUrl() {
        return pageImageUrl;
    }

    public void setPageImageUrl(String pageImageUrl) {
        this.pageImageUrl = pageImageUrl;
    }
}
