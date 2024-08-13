package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.primeplay.faithflix.OnTimeClickListener;
import com.primeplay.faithflix.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class TimeIntervalAdapter extends RecyclerView.Adapter<TimeIntervalAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> timeIntervals;
    private final String currentTimePoint;
    private final OnTimeClickListener onTimeClickListener;

    public TimeIntervalAdapter(Context context, ArrayList<String> timeIntervals, OnTimeClickListener onTimeClickListener) {
        this.context = context;
        this.timeIntervals = timeIntervals;
        this.onTimeClickListener = onTimeClickListener;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();
        String currentTime = sdf.format(now);
        this.currentTimePoint = getCurrentTimePoint(currentTime);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_interval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String timePoint = timeIntervals.get(position);
        holder.timeButton.setText(timePoint);

        if (timePoint.equals(currentTimePoint)) {
            holder.timeButton.setBackgroundColor(Color.BLUE);
            holder.timeButton.setTextColor(Color.WHITE);
            holder.timeButton.setStrokeColor(ColorStateList.valueOf(Color.BLUE));
        } else {
            holder.timeButton.setBackgroundColor(Color.GRAY);
            holder.timeButton.setTextColor(Color.BLACK);
            holder.timeButton.setStrokeColor(ColorStateList.valueOf(Color.GRAY));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onTimeClickListener != null) {
                onTimeClickListener.onTimeClick(timePoint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeIntervals.size();
    }

    private String getCurrentTimePoint(String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date now = sdf.parse(currentTime);
            Date startTime = sdf.parse("00:00");
            Date endTime = sdf.parse("23:30");

            while (startTime.before(endTime)) {
                Date nextTime = new Date(startTime.getTime() + 30 * 60 * 1000);
                if (now.after(startTime) && now.before(nextTime)) {
                    return sdf.format(startTime);
                }
                startTime = nextTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void scrollToCurrentTime(RecyclerView recyclerView) {
        if (currentTimePoint.isEmpty()) return;

        int position = timeIntervals.indexOf(currentTimePoint);
        if (position != -1) {
            recyclerView.scrollToPosition(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton timeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeButton = itemView.findViewById(R.id.time_button);
        }
    }
}
