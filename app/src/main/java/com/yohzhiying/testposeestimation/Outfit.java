package com.yohzhiying.testposeestimation;

public class Outfit {

    String name, category, imageUrl, description;
    byte[] image;

    public Outfit() {}

    public Outfit(String category, String name, String imageUrl, String description) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Outfit(String category, String name, byte[] image) {
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public String getOutfitCategory() {
        return category;
    }

    public String getOutfitName() {
        return name;
    }

    public String getOutfitUrl() {
        return imageUrl;
    }

    public byte[] getImage() {
        return image;
    }

    public String getOutfitDescription() {
        return description;
    }

    public void setOutfitCategory(String category) {
        this.category = category;
    }

    public void setOutfitName(String name) {
        this.name = name;
    }

    public void setOutfitUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOutfitDescription(String description) {
        this.description = description;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
