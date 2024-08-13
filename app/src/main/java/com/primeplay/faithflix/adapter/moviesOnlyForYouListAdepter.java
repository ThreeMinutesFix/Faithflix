package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.Ui.MovieDetails;
import com.primeplay.faithflix.models.MovieList;
import com.primeplay.faithflix.util.AppConfig;

import java.util.List;

public class moviesOnlyForYouListAdepter extends RecyclerView.Adapter<moviesOnlyForYouListAdepter.MyViewHolder> {
    private Context mContext;
    private List<MovieList> mData;

    Context context;

    public moviesOnlyForYouListAdepter(Context mContext, List<MovieList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(AppConfig.contentItem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));


        holder.Movie_Item.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("ID", mData.get(position).getID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Title;
        TextView Year;
        ImageView Thumbnail;
        View Premium_Tag;
        CardView Movie_Item;
        CardView Show_All;
        TextView tag_text;
        CardView tag_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);
            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);
            Movie_Item = itemView.findViewById(R.id.Movie_Item);

            tag_card = itemView.findViewById(R.id.tag_card);
            tag_text = itemView.findViewById(R.id.tag_text);
        }


        void setTitle(MovieList title_text) {
            Title.setText(title_text.getTitle());
        }

        void setYear(MovieList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(MovieList Thumbnail_Image) {

            Glide.with(context)
                    .load(Thumbnail_Image.getThumbnail())
                    .into(Thumbnail);

        }


    }
}
