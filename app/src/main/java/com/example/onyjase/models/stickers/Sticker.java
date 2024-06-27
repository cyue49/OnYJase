package com.example.onyjase.models.stickers;

public class Sticker {
    private String id;
    private String title;
    private StickerImages images;

    // constructor
    public Sticker(String id, String title, StickerImages images) {
        this.id = id;
        this.title = title;
        this.images = images;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public StickerImages getImages() {
        return images;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImages(StickerImages images) {
        this.images = images;
    }
}
