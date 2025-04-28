// api/PixabayApiService.java
package com.example.ImageGallery.api;

import com.example.ImageGallery.models.PixabayResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PixabayApiService {
    @GET("api/")
    Call<PixabayResponse> searchImages(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("page") int page,
            @Query("per_page") int perPage
    );
}