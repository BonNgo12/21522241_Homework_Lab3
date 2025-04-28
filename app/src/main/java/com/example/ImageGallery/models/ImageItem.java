package com.example.ImageGallery.models;
import com.google.gson.annotations.SerializedName;

public class ImageItem {

    // Unique identifier for the image.
    @SerializedName("id")
    private int id;

    // The URL for the preview version of the image (small-sized image).
    @SerializedName("previewURL")
    private String previewURL;

    // The URL for the full-sized image (large version).
    @SerializedName("largeImageURL")
    private String largeImageURL;

    // Tags associated with the image.
    @SerializedName("tags")
    private String tags;
    public int getId() {
        return id;
    }
    public String getPreviewURL() {
        return previewURL;
    }
    public String getLargeImageURL() {
        return largeImageURL;
    }
    public String getTags() {
        return tags;
    }
}
