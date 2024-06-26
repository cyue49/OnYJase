package com.example.onyjase.models.stickers;

public class StickerImages {
    private StickerImage original;

    // constructor
    public StickerImages(StickerImage original) {
        this.original = original;
    }

    // getters and setters
    public StickerImage getOriginal() {
        return original;
    }

    public void setOriginal(StickerImage original) {
        this.original = original;
    }
}
