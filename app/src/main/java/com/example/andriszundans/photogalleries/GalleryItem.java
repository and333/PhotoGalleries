package com.example.andriszundans.photogalleries;


import com.google.gson.annotations.SerializedName;

public class GalleryItem {

    @SerializedName("title")
    private String caption;
    @SerializedName("id")
    private String id;
    @SerializedName("url_s")
    private String url;


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return caption;
    }

}
