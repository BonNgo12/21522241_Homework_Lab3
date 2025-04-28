package com.example.ImageGallery.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.ImageGallery.api.PixabayApiService;
import com.example.ImageGallery.models.ImageItem;
import com.example.ImageGallery.models.PixabayResponse;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ImagePagingSource extends RxPagingSource<Integer, ImageItem> {
    // Constant for the starting page index when fetching data.
    private static final int STARTING_PAGE_INDEX = 1;
    // The API key for authenticating requests to the Pixabay API.
    private static final String API_KEY = "49985359-185aeb212462889ef917d2103";
    // Number of items per page to load.
    private static final int PER_PAGE = 20;

    // The Pixabay API service to make the requests.
    private final PixabayApiService apiService;
    // The search query used to filter the images.
    private final String query;


    public ImagePagingSource(PixabayApiService apiService, String query) {
        this.apiService = apiService;
        this.query = query;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, ImageItem>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        // Determine the current page to load. Default to the starting page if no key is provided.
        int page = loadParams.getKey() != null ? loadParams.getKey() : STARTING_PAGE_INDEX;

        return Single.fromCallable(() -> {
            try {
                // Make the API call to fetch images for the given page
                Call<PixabayResponse> call = apiService.searchImages(API_KEY, query, page, PER_PAGE);
                Response<PixabayResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    PixabayResponse data = response.body();
                    // Calculate next key
                    Integer nextKey = null;
                    if (!data.getHits().isEmpty() && data.getHits().size() >= PER_PAGE) {
                        nextKey = page + 1;
                    }
                    // Return the loaded page with the image items, previous page key, and next page key.
                    return new LoadResult.Page<Integer, ImageItem>(
                            data.getHits(),
                            page > STARTING_PAGE_INDEX ? page - 1 : null,
                            nextKey
                    );
                } else {
                    // If the API response was not successful, return an error
                    return new LoadResult.Error<Integer, ImageItem>(new IOException("API error " + response.code()));
                }
            } catch (Exception e) {
                return new LoadResult.Error<Integer, ImageItem>(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, ImageItem> state) {
        // Retrieve the anchor position (the current position the user is viewing).
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null; // If there is no anchor position, return null.
        }

        // Find the closest page to the anchor position.
        LoadResult.Page<Integer, ImageItem> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null; // If no page is found, return null.
        }

        // If the anchor page has a previous key, return the previous page key incremented by 1.
        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1; // Return the previous page key plus one for refreshing.
        }

        // If the anchor page has a next key, return the next page key minus one.
        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1; // Return the next page key minus one for refreshing.
        }

        return null; // If neither prevKey nor nextKey is available, return null.
    }
}
