package com.boostcamp.dreampicker.model;

import java.util.List;

public class Image {
    private String imageId;
    private int image;
    // TODO : 이후 URL로 변경 예정
    private String imageUrl;
    // TODO : 이후 TagGroup 적용
    private String tag;
    private List<String> tagList;

    public Image() {
    }

    public Image(String id, int image, String tag) {
        this.imageId = id;
        this.image = image;
        this.tag = tag;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}