package com.example.ImageGallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ImageGallery.R;
import com.example.ImageGallery.models.ImageItem;
import com.example.ImageGallery.viewmodel.ImageViewModel;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class ImagePagingAdapter extends PagingDataAdapter<ImageItem, ImagePagingAdapter.ImageViewHolder> {

    private final ImageViewModel viewModel;

    public ImagePagingAdapter(ViewModelStoreOwner viewModelStoreOwner) {
        super(COMPARATOR);
        this.viewModel = new ViewModelProvider(viewModelStoreOwner).get(ImageViewModel.class);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    // In your ImagePagingAdapter.java, modify the onBindViewHolder method:
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem item = getItem(position);
        if (item != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPreviewURL())
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .into(holder.imageView);

            // Set initial tags
            holder.tagsTextView.setText(item.getTags());

            // Reference the textView for clarity
            final TextView textView = holder.tagsTextView;

            // Analyze image and update tags
            viewModel.analyzeImage(item.getPreviewURL()).thenAccept(labels -> {
                if (labels != null && !labels.isEmpty()) {
                    String originalTags = item.getTags();
                    String aiLabels = String.join(", ", labels);

                    // Use SpannableString to style the text
                    SpannableString combinedText = new SpannableString(originalTags + "\nDetected: " + aiLabels);

                    int aiStart = originalTags.length() + 1; // +1 for the newline
                    int aiEnd = aiStart + "Detected:".length();
                    combinedText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), aiStart, aiEnd, 0);
                    combinedText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), aiStart, aiEnd, 0);

                    combinedText.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")),
                            aiEnd, combinedText.length(), 0);

                    ((AppCompatActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                        if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION
                                && getItem(holder.getBindingAdapterPosition()) == item) {
                            textView.setText(combinedText);
                            textView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tagsTextView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            tagsTextView = itemView.findViewById(R.id.tags_text_view);
        }
    }

    private static final DiffUtil.ItemCallback<ImageItem> COMPARATOR =
            new DiffUtil.ItemCallback<ImageItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ImageItem oldItem, @NonNull ImageItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ImageItem oldItem, @NonNull ImageItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }
            };
}