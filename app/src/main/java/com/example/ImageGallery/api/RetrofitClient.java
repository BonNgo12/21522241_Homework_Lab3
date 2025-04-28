package com.example.ImageGallery.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Base URL for the API (Pixabay in this case)
    private static final String BASE_URL = "https://pixabay.com/";
    // Singleton Retrofit instance
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create a logging interceptor to log HTTP request and response bodies
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);  // Log request/response body

            // Build an OkHttpClient with the logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)  // Add the interceptor to the client
                    .build();

            // Build Retrofit instance with the OkHttp client and Gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Set the base URL for the API
                    .addConverterFactory(GsonConverterFactory.create())  // Convert JSON responses to Java objects using Gson
                    .client(client)  // Use the OkHttp client with the logging interceptor
                    .build();  // Build the Retrofit instance
        }
        return retrofit;  // Return the Retrofit instance
    }
}
