package com.islamicApp.AlFurkan.Modules;

public class AzkarListenModel {

    String zekrName, zekrLink;

    public AzkarListenModel() {
    }

    public AzkarListenModel(String zekrName, String zekrLink) {
        this.zekrName = zekrName;
        this.zekrLink = zekrLink;
    }

    public String getZekrName() {
        return zekrName;
    }

    public void setZekrName(String zekrName) {
        this.zekrName = zekrName;
    }

    public String getZekrLink() {
        return zekrLink;
    }

    public void setZekrLink(String zekrLink) {
        this.zekrLink = zekrLink;
    }
}
