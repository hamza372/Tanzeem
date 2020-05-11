package com.thinkdone.tanzeem.DataModels;

public class KitabDataModel {
    int bookId;
    String bookName;
    String publisherName;
    String publisherDetails;
    String publishVersion;
    String publishYear;
    int bookAuthorId;
    int bookCategoryId;
    String urduName;
    String urduPublisherName;
    String urduPublisherDetails;
    String bookImage;
    String downloadLink;
    String fileName;
    String kitabNameEng;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public KitabDataModel() {
    }

    public KitabDataModel(int bookId, String bookName, String publisherName, String publisherDetails, String publishVersion, String publishYear, int bookAuthorId, int bookCategoryId, String urduName, String urduPublisherName, String urduPublisherDetails, String bookImage, String downloadLink) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.publisherName = publisherName;
        this.publisherDetails = publisherDetails;
        this.publishVersion = publishVersion;
        this.publishYear = publishYear;
        this.bookAuthorId = bookAuthorId;
        this.bookCategoryId = bookCategoryId;
        this.urduName = urduName;
        this.urduPublisherName = urduPublisherName;
        this.urduPublisherDetails = urduPublisherDetails;
        this.bookImage = bookImage;
        this.downloadLink = downloadLink;
    }

    public String getKitabNameEng() {
        return kitabNameEng;
    }

    public void setKitabNameEng(String kitabNameEng) {
        this.kitabNameEng = kitabNameEng;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherDetails() {
        return publisherDetails;
    }

    public void setPublisherDetails(String publisherDetails) {
        this.publisherDetails = publisherDetails;
    }

    public String getPublishVersion() {
        return publishVersion;
    }

    public void setPublishVersion(String publishVersion) {
        this.publishVersion = publishVersion;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public int getBookAuthorId() {
        return bookAuthorId;
    }

    public void setBookAuthorId(int bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public int getBookCategoryId() {
        return bookCategoryId;
    }

    public void setBookCategoryId(int bookCategoryId) {
        this.bookCategoryId = bookCategoryId;
    }

    public String getUrduName() {
        return urduName;
    }

    public void setUrduName(String urduName) {
        this.urduName = urduName;
    }

    public String getUrduPublisherName() {
        return urduPublisherName;
    }

    public void setUrduPublisherName(String urduPublisherName) {
        this.urduPublisherName = urduPublisherName;
    }

    public String getUrduPublisherDetails() {
        return urduPublisherDetails;
    }

    public void setUrduPublisherDetails(String urduPublisherDetails) {
        this.urduPublisherDetails = urduPublisherDetails;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
