package com.pwc.sentinel;

public class UploadImage {

    private String mImageUrl;

    public UploadImage() {

    }

    public UploadImage(String name, String urlpic) {

        mImageUrl = urlpic;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
