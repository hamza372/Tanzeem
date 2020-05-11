package com.thinkdone.tanzeem.DataModels;

public class AuthorDataModel {
    int author_Id;
    String nameUrdu;
    String details;

    public AuthorDataModel() {
    }

    public AuthorDataModel(int author_Id, String nameUrdu, String details) {
        this.author_Id = author_Id;
        this.nameUrdu = nameUrdu;
        this.details = details;
    }

    public int getAuthor_Id() {
        return author_Id;
    }

    public void setAuthor_Id(int author_Id) {
        this.author_Id = author_Id;
    }

    public String getNameUrdu() {
        return nameUrdu;
    }

    public void setNameUrdu(String nameUrdu) {
        this.nameUrdu = nameUrdu;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
