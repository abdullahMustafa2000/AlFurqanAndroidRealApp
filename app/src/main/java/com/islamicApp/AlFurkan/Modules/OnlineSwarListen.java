package com.islamicApp.AlFurkan.Modules;

public class OnlineSwarListen {
    String suraName, qareeName;
    int suraPos, qareePos;
    String qareeLink, suraLink;

    public OnlineSwarListen(String suraName, String qareeName, int suraPos, int qareePos, String qareeLink, String suraLink) {
        this.suraName = suraName;
        this.qareeName = qareeName;
        this.suraPos = suraPos;
        this.qareePos = qareePos;
        this.qareeLink = qareeLink;
        this.suraLink = suraLink;
    }

    public OnlineSwarListen() {
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

    public int getSuraPos() {
        return suraPos;
    }

    public void setSuraPos(int suraPos) {
        this.suraPos = suraPos;
    }

    public int getQareePos() {
        return qareePos;
    }

    public void setQareePos(int qareePos) {
        this.qareePos = qareePos;
    }

    public String getQareeLink() {
        return qareeLink;
    }

    public void setQareeLink(String qareeLink) {
        this.qareeLink = qareeLink;
    }

    public String getSuraLink() {
        return suraLink;
    }

    public void setSuraLink(String suraLink) {
        this.suraLink = suraLink;
    }
}
