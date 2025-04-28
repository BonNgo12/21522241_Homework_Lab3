package com.example.ImageGallery.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PixabayResponse {
    // The total number of images available based on the search query.
    @SerializedName("total")
    private int total;

    // The total number of image hits returned for the current page of results.
    @SerializedName("totalHits")
    private int totalHits;

    // The list of image items returned from the search query.
    @SerializedName("hits")
    private List<ImageItem> hits;

    public int getTotal() {
        return total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public List<ImageItem> getHits() {
        return hits;
    }
}