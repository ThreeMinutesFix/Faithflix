package com.primeplay.faithflix.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.Ui.MovieDetails;
import com.primeplay.faithflix.Ui.WebSeriesDetails;
import com.primeplay.faithflix.models.ImageSliderItem;
import com.primeplay.faithflix.util.AppConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageSliderAdepter extends RecyclerView.Adapter<ImageSliderAdepter.SliderViewHolder> {

    private final List<ImageSliderItem> slider_items;
    private final ViewPager2 viewPager2;
    boolean isFavourite = false;
    Context context;
    private String tempUserID;


    @SuppressLint("HardwareIds")
    public ImageSliderAdepter(List<ImageSliderItem> slider_items, ViewPager2 viewPager2, int userID) {
        this.slider_items = slider_items;
        this.viewPager2 = viewPager2;
        if (userID != 0) {
            tempUserID = String.valueOf(userID);
        } else {
            tempUserID = "12";
        }
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_slider_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(slider_items.get(position));

        if (position == slider_items.size() - 2) {
            viewPager2.post(runnable);
        }
//        holder.setyear(slider_items.get(position));
        holder.setImage(slider_items.get(position));

        holder.favView.setOnClickListener(view ->
        {
            if (slider_items.get(position).getContent_Type() == 0) {
                restoreFavouriteState(position, holder.favView); // Restore favorite state first
                if (isFavourite) {
                    removeFavourite(position, holder.favView);
                } else {
                    setFavourite(position, holder.favView);
                }

            } else if (slider_items.get(position).getContent_Type() == 1) {
//                Intent intent = new Intent(context, WebSeriesDetails.class);
//                intent.putExtra("ID", slider_items.get(position).getContent_ID());
//                context.startActivity(intent);
            } else if (slider_items.get(position).getContent_Type() == 2) {
                Intent intent = new Intent(context, WebView.class);
                intent.putExtra("URL", slider_items.get(position).getURL());
                context.startActivity(intent);

            } else if (slider_items.get(position).getContent_Type() == 3) {
                String URL = slider_items.get(position).getURL();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });

        if (position == slider_items.size() - 2) {
            viewPager2.post(runnable);
        }
        holder.play_btn_slider.setOnClickListener(v ->
        {

            if (slider_items.get(position).getContent_Type() == 0) {
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("ID", slider_items.get(position).getContent_ID());
                context.startActivity(intent);
            } else if (slider_items.get(position).getContent_Type() == 1) {
                Intent intent = new Intent(context, WebSeriesDetails.class);
                intent.putExtra("ID", slider_items.get(position).getContent_ID());
                context.startActivity(intent);
            } else if (slider_items.get(position).getContent_Type() == 2) {
                Intent intent = new Intent(context, WebView.class);
                intent.putExtra("URL", slider_items.get(position).getURL());
                context.startActivity(intent);

            } else if (slider_items.get(position).getContent_Type() == 3) {
                String URL = slider_items.get(position).getURL();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });
    }

    private void removeFavourite(int position, ImageView moreview) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "favourite/REMOVE/" + tempUserID + "/Movie/" + slider_items.get(position).getContent_ID(), response -> {
            if (response.equals("Favourite successfully Removed")) {
                isFavourite = false;
//                moreview.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_add_24));
            }

        }, error -> {
            // Do nothing
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }


    @Override
    public int getItemCount() {
        return Math.min(slider_items.size(), 15);
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView logoview;
        private RoundedImageView poster_view;
        private MaterialButton play_btn_slider;
        private ImageView favView;
        private TextView year, genres, languages;


        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            logoview = itemView.findViewById(R.id.logoview);
            poster_view = itemView.findViewById(R.id.banner_bg);
            play_btn_slider = itemView.findViewById(R.id.play_btn_slider);
            favView = itemView.findViewById(R.id.Favorite_view);

        }

        void setImage(ImageSliderItem image_slider_item) {
            Glide.with(context)
                    .load(image_slider_item.getLogo_imgs())
                    .into(logoview);
            Glide.with(context)
                    .load(image_slider_item.getImage())
                    .into(poster_view);
        }



    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slider_items.addAll(slider_items);
            notifyDataSetChanged();
        }
    };

    void setFavourite(int position, ImageView moreview) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "favourite/SET/" + tempUserID + "/Movie/" + slider_items.get(position).getContent_ID(), response -> {
            if (response.equals("New favourite created successfully")) {
                isFavourite = true;
                // Assuming moreview is your MaterialButton
                saveFavouriteState(String.valueOf(slider_items.get(position).getContent_ID()), true);
//                moreview.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_check_24));
                Toast.makeText(context, "item added to favorites successfully", Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            // Do nothing
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    void saveFavouriteState(String contentId, boolean isFavorite) {
        SharedPreferences preferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(contentId, isFavorite);
        editor.apply();
    }

    void restoreFavouriteState(int position, ImageView moreview) {
        SharedPreferences preferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        String contentId = String.valueOf(slider_items.get(position).getContent_ID());
        boolean isFavorite = preferences.getBoolean(contentId, false);
        if (isFavorite) {
//            moreview.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_check_24));
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SliderViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            searchFavorites(holder.favView, position);
        }
    }

    private void searchFavorites(ImageView moreview, int position) {
        RequestQueue queue = Volley.newRequestQueue(moreview.getContext());
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "favourite/SEARCH/" + tempUserID + "/Movie/" + slider_items.get(position).getContent_ID(), response -> {
            if (response.equals("Record Found")) {
                isFavourite = true;
//                moreview.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_check_24));
            }

        }, error -> {
            // Do nothing
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

}
