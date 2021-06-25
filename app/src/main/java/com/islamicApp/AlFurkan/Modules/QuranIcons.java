package com.islamicApp.AlFurkan.Modules;

public class QuranIcons {

    String suraName;
    int suraPosition;

    public QuranIcons(String suraName, int suraPosition) {
        this.suraName = suraName;
        this.suraPosition = suraPosition;
    }

    public String getSuraName() {
        return suraName;
    }

    public void setSuraName(String suraName) {
        this.suraName = suraName;
    }

    public int getSuraPosition() {
        return suraPosition;
    }

    public void setSuraPosition(int suraPosition) {
        this.suraPosition = suraPosition;
    }
}
