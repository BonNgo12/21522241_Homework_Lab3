package com.example.ImageGallery.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.ImageGallery.utils.ObjectDetectionModel;
import com.example.ImageGallery.api.PixabayApiService;
import com.example.ImageGallery.api.RetrofitClient;
import com.example.ImageGallery.models.ImageItem;
import com.example.ImageGallery.paging.ImagePagingSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ImageViewModel extends AndroidViewModel {

    private final PixabayApiService apiService; // API service instance to fetch images
    private final Map<String, LiveData<PagingData<ImageItem>>> searchCache = new HashMap<>(); // Cache for storing search results
    private static final int PAGE_SIZE = 20; // Number of items per page for pagination
    private final ObjectDetectionModel imageAnalyzer; // AI model for object detection in images

    public ImageViewModel(@NonNull Application application) {
        super(application);
        // Initialize API service
        apiService = RetrofitClient.getClient().create(PixabayApiService.class);
        // Initialize object detection model
        imageAnalyzer = new ObjectDetectionModel(application.getApplicationContext());
    }

    public LiveData<PagingData<ImageItem>> searchImages(String query) {
        // Check if the result for this query is already cached
        if (searchCache.containsKey(query)) {
            return searchCache.get(query);
        }

        // Create a new Pager for fetching data
        Pager<Integer, ImageItem> pager = new Pager<>(
                new PagingConfig(PAGE_SIZE, PAGE_SIZE, false), // Configure page size
                () -> new ImagePagingSource(apiService, query) // Data source
        );

        // Convert Pager to LiveData and cache it in the ViewModel scope
        LiveData<PagingData<ImageItem>> liveData = PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                ViewModelKt.getViewModelScope(this)
        );

        // Store the result in cache
        searchCache.put(query, liveData);
        return liveData;
    }

    public CompletableFuture<List<String>> analyzeImage(String imageUrl) {
        return imageAnalyzer.analyzeImage(imageUrl);
    }
}
