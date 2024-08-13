package com.primeplay.faithflix.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.models.ImageSliderItem;

import java.util.List;


public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    private List<ImageSliderItem> sliderItems;
    private ViewPager2 viewPager2;
    private int selectedPosition = -1;

    public ThumbnailAdapter(List<ImageSliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail, parent, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        holder.bind(sliderItems.get(position));

        holder.itemView.setOnClickListener(v -> {
            // Update ViewPager2 when thumbnail is clicked
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            viewPager2.setCurrentItem(position, true);

        });
        if (position == selectedPosition) {
            holder.cardView.setStrokeColor(holder.itemView.getContext().getResources().getColor(R.color.gray_back));
        } else {
            holder.cardView.setStrokeColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class ThumbnailViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnailImageView;
        MaterialCardView cardView;

        public ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail_image);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(ImageSliderItem item) {
            Glide.with(itemView.getContext())
                    .load(item.getImage())
                    .into(thumbnailImageView);
        }
    }
    public void updateSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

}
