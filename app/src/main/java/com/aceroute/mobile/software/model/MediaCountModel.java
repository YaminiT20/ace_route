package com.aceroute.mobile.software.model;

public class MediaCountModel {

    Integer imgCount;
    Integer sigCount;
    Integer audCount;
    Integer fileCount;

    public MediaCountModel(Integer imgCount, Integer sigCount, Integer audCount, Integer fileCount){
        this.imgCount = imgCount;
        this.sigCount = sigCount;
        this.audCount = audCount;
        this.fileCount = fileCount;
    }

    public Integer getImgCount() {
        return imgCount;
    }

    public void setImgCount(Integer imgCount) {
        this.imgCount = imgCount;
    }

    public Integer getSigCount() {
        return sigCount;
    }

    public void setSigCount(Integer sigCount) {
        this.sigCount = sigCount;
    }

    public Integer getAudCount() {
        return audCount;
    }

    public void setAudCount(Integer audCount) {
        this.audCount = audCount;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }
}
