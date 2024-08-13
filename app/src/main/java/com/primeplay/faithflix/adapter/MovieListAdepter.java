package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class MovieListAdepter extends RecyclerView.Adapter<MovieListAdepter.MyViewHolder> {

    private Context mContext;
    private List<MovieList> mData;

    public MovieListAdepter(Context mContext, List<MovieList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setImage(mData.get(position));

        holder.Movie_Item.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("ID", mData.get(position).getID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size(); // No extra item for "Show All"
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Title;
        TextView Year;
        ImageView Thumbnail;

        CardView Movie_Item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.Movie_list_Title);
            Year = itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = itemView.findViewById(R.id.Movie_Item_thumbnail);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);
        }

        public void setTitle(MovieList movieList) {
            Title.setText(movieList.getTitle());
        }

        public void setImage(MovieList movieList) {
            Glide.with(mContext)
                    .load(movieList.getThumbnail())
                    .into(Thumbnail);
        }
    }
}
