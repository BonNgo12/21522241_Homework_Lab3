package com.example.ImageGallery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ImageGallery.adapter.ImagePagingAdapter;
import com.example.ImageGallery.viewmodel.ImageViewModel;

public class MainActivity extends AppCompatActivity {
    private ImageViewModel viewModel;
    private ImagePagingAdapter adapter;
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final Handler searchDebounceHandler = new Handler(Looper.getMainLooper());
    private static final long SEARCH_DEBOUNCE_DELAY = 500;
    private static final String DEFAULT_SEARCH_QUERY = "sky";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        searchEditText = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.images_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        // Setup RecyclerView
        adapter = new ImagePagingAdapter(this); // Pass 'this' as ViewModelStoreOwner
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        setupSearch();

        // Initial search
        searchImages(DEFAULT_SEARCH_QUERY);
    }

    private void setupSearch() {
        // Handle keyboard search action
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchEditText.getText().toString().trim();
                    if (!query.isEmpty()) {
                        searchImages(query);
                    }
                    return true;
                }
                return false;
            }
        });

        // Smart search with debounce
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDebounceHandler.removeCallbacksAndMessages(null);

                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchDebounceHandler.postDelayed(() -> searchImages(query), SEARCH_DEBOUNCE_DELAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchImages(String query) {
        progressBar.setVisibility(View.VISIBLE);

        viewModel.searchImages(query).observe(this, pagingData -> {
            progressBar.setVisibility(View.GONE);
            adapter.submitData(getLifecycle(), pagingData);
        });
    }
}