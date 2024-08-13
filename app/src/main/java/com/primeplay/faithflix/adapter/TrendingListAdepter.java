package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.Ui.MovieDetails;
import com.primeplay.faithflix.Ui.WebSeriesDetails;
import com.primeplay.faithflix.models.TrendingList;
import com.primeplay.faithflix.util.AppConfig;


import java.util.List;

public class TrendingListAdepter extends RecyclerView.Adapter<TrendingListAdepter.MyViewHolder> {
    private Context mContext;
    private List<TrendingList> mData;

    public TrendingListAdepter(Context mContext, List<TrendingList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setImage(mData.get(position));
        holder.setNo(position);
        holder.setCustomTag(mData.get(position));

        holder.Trending_Item_contenar.setOnClickListener(view -> {
            if(mData.get(position).getContentType()==1) {
                Intent intent = new Intent(mContext, MovieDetails.class);
                intent.putExtra("ID", mData.get(position).getID());
                mContext.startActivity(intent);
            } else if(mData.get(position).getContentType()==2) {
                Intent intent = new Intent(mContext, WebSeriesDetails.class);
                intent.putExtra("ID", mData.get(position).getID());
                mContext.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView Trending_Item_thumbnail;

        LinearLayout Trending_Item_contenar;
        ImageView no_image, no_10_image;
        CardView tag_card;
        TextView tag_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Trending_Item_thumbnail = itemView.findViewById(R.id.Trending_Item_thumbnail);

            Trending_Item_contenar = itemView.findViewById(R.id.Trending_Item_contenar);
            no_image = itemView.findViewById(R.id.no_image);
            no_10_image = itemView.findViewById(R.id.no_10_image);
            tag_card = itemView.findViewById(R.id.tag_card);
            tag_text = itemView.findViewById(R.id.tag_text);
        }

        void setImage(TrendingList Thumbnail_Image) {
            Glide.with(mContext)
                    .load(Thumbnail_Image.getThumbnail())
                    .into(Trending_Item_thumbnail);
        }


        void setNo(int position) {
            if(position==0) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_1));
            } else if(position==1) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_2));
            } else if(position==2) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_3));
            } else if(position==3) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_4));
            } else if(position==4) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_5));
            } else if(position==5) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_6));
            } else if(position==6) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_7));
            } else if(position==7) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_8));
            } else if(position==8) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_9));
            } else if(position==9) {
                no_image.setVisibility(View.GONE);
                no_10_image.setVisibility(View.VISIBLE);
                no_10_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_10));
            }

        }

        void setCustomTag(TrendingList trendingList) {
            if (!trendingList.getCustom_tag().isEmpty()) {
                tag_text.setText(trendingList.getCustom_tag());
                tag_card.setVisibility(View.VISIBLE);
                tag_text.setTextColor(Color.parseColor(trendingList.getCustom_tag_text_color()));
                tag_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(trendingList.getCustom_tag_background_color())));
            } else {
                tag_card.setVisibility(View.GONE);
            }
        }
    }
}
