package com.islamicApp.AlFurkan.db.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "marked_aya")
public class SqliteAyaModel {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private int ayaId, ayaNum, pageNum, suraNum, chapterNum, isLastAyaOnPage, isMarked;
    private String suraName, ayaNormalText, ayaTashkelText, enAyah;

    public int getIsMarked() {
        return isMarked;
    }

    public void setIsMarked(int isMarked) {
        this.isMarked = isMarked;
    }

    public int getIsLastAyaOnPage() {
        return isLastAyaOnPage;
    }

    public void setIsLastAyaOnPage(int isLastAyaOnPage) {
        this.isLastAyaOnPage = isLastAyaOnPage;
    }

    public SqliteAyaModel() {
    }

    public int getAyaId() {
        return ayaId;
    }

    public void setAyaId(int ayaId) {
        this.ayaId = ayaId;
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

    public int getSuraNum() {
        return suraNum;
    }

    public void setSuraNum(int suraNum) {
        this.suraNum = suraNum;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getSuraName() {
        return suraName;
    }

    public void setSuraName(String suraName) {
        this.suraName = suraName;
    }

    public String getAyaNormalText() {
        return ayaNormalText;
    }

    public void setAyaNormalText(String ayaNormalText) {
        this.ayaNormalText = ayaNormalText;
    }

    public String getAyaTashkelText() {
        return ayaTashkelText;
    }

    public void setAyaTashkelText(String ayaTashkelText) {
        this.ayaTashkelText = ayaTashkelText;
    }

    public String getEnAyah() {
        return enAyah;
    }

    public void setEnAyah(String enAyah) {
        this.enAyah = enAyah;
    }


}
