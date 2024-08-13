package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.models.Program;

import java.util.List;
import java.util.Map;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {
    private List<Program> programList;
    private Context context;
    private int currentPlayingPosition = -1;

    public ProgramAdapter(List<Program> programList, Context context, int currentPlayingPosition) {
        this.programList = programList;
        this.context = context;
        this.currentPlayingPosition = currentPlayingPosition;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_item, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        Program program = programList.get(position);
        holder.programName.setText(program.getProgramName());
        holder.programTime.setText(program.getProgramTime());
        Glide.with(context).load(program.getImageUrl()).into(holder.programImage);

        // Highlight the currently playing program
        if (position == currentPlayingPosition) {
            holder.selectedItems.setVisibility(View.VISIBLE);
        } else {
            holder.selectedItems.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public List<Program> getProgramList() {
        return programList;
    }

    public static class ProgramViewHolder extends RecyclerView.ViewHolder {
        TextView programName, programTime;
        ImageView programImage;
        MaterialCardView selectedItems;

        public ProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            programName = itemView.findViewById(R.id.programName);
            programTime = itemView.findViewById(R.id.programTime);
            programImage = itemView.findViewById(R.id.programImage);
            selectedItems = itemView.findViewById(R.id.selectedItems);
        }
    }
}

