package com.islamicApp.AlFurkan.Modules;

public class LastAyaInPage {
    int ayaNum, pageNum;
    String ayaTashkel;

    public LastAyaInPage(int ayaNum, int pageNum, String ayaTashkel) {
        this.ayaNum = ayaNum;
        this.pageNum = pageNum;
        this.ayaTashkel = ayaTashkel;
    }

    public LastAyaInPage() {
    }

    public int getAyaNum() {
        return ayaNum;
    }

    public void setAyaNum(int ayaNum) {
        this.ayaNum = ayaNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getAyaTashkel() {
        return ayaTashkel;
    }

    public void setAyaTashkel(String ayaTashkel) {
        this.ayaTashkel = ayaTashkel;
    }
}
