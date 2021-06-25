package com.islamicApp.AlFurkan.Modules;

public class QuranListenSuggestionsModel {
    int itemID;
    String suraName, qareeName, suraLink, suraPos;

    public QuranListenSuggestionsModel(String suraName, String qareeName, String suraLink, String suraPos) {
        this.suraName = suraName;
        this.qareeName = qareeName;
        this.suraLink = suraLink;
        this.suraPos = suraPos;
    }

    public QuranListenSuggestionsModel() {
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getSuraName() {
        return suraName;
    }

    public void setSuraName(String suraName) {
        this.suraName = suraName;
    }

    public String getQareeName() {
        return qareeName;
    }

    public void setQareeName(String qareeName) {
        this.qareeName = qareeName;
    }

    public String getSuraLink() {
        return suraLink;
    }

    public void setSuraLink(String suraLink) {
        this.suraLink = suraLink;
    }

    public String getSuraPos() {
        return suraPos;
    }

    public void setSuraPos(String suraPos) {
        this.suraPos = suraPos;
    }
}
