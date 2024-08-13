package com.primeplay.faithflix.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.cardview.widget.CardView;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.Ui.LiveTVPlayer;
import com.primeplay.faithflix.models.LiveTvChannelList;
import com.primeplay.faithflix.util.AppConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class LiveTvChannelListAdepter extends RecyclerView.Adapter<LiveTvChannelListAdepter.myViewHolder> {
    private Context mContext;
    private List<LiveTvChannelList> mData;

    public LiveTvChannelListAdepter(Context mContext, List<LiveTvChannelList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(AppConfig.live_tv_channel_item, parent, false);
        return new myViewHolder(view);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        if (position < mData.size()) {
            holder.setTitle(mData.get(position));
            holder.setImage(mData.get(position));

            holder.live_tv_channel_Item.setOnClickListener(view -> {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getLiveTVDetails/" + mData.get(position).getID(), response -> {
                    JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                    handleLiveTVClick(jsonObject, position);
                }, error -> {
                    // Handle error
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-api-key", AppConfig.apiKey);
                        return params;
                    }
                };
                queue.add(sr);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        ImageView Banner;
        CardView live_tv_channel_Item;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.Live_Tv_Title);
            Banner = itemView.findViewById(R.id.Live_Tv_Banner);
            live_tv_channel_Item = itemView.findViewById(R.id.live_tv_channel_Item);
        }

        void setTitle(LiveTvChannelList title_text) {
            Title.setText(title_text.getName());
            fetchCurrentProgram(title_text.getName(), Title, Banner);
        }

        void setImage(LiveTvChannelList Banner_Image) {
            Glide.with(mContext)
                    .load(Banner_Image.getBanner())
                    .into(Banner);
        }
    }

    private void fetchCurrentProgram(String channelName, TextView Title, ImageView Banner) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "https://tvschedules.threeminutesfix.in/api.php?channelname=" + Uri.encode(channelName) + "&apikey=1a6ba61a2f2f8f1ff0d69b787a76c520";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("Mydata", "" + response);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                JsonObject currentProgram = getCurrentProgram(jsonArray);
                if (currentProgram != null) {
                    String programName = currentProgram.get("program_name").getAsString();
                    String imageUrl = currentProgram.get("image_url").getAsString();
                    Title.setText(programName);
                    Glide.with(mContext)
                            .load(imageUrl)
                            .into(Banner);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle the error
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        queue.add(request);
    }

    private JsonObject getCurrentProgram(JsonArray programs) {
        // Example implementation, adjust based on is_static_schedule
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        for (JsonElement element : programs) {
            JsonObject program = element.getAsJsonObject();
            String startTime = program.get("start_time").getAsString();
            String endTime = program.get("end_time").getAsString();
            boolean isStaticSchedule = program.has("is_static_schedule") && program.get("is_static_schedule").getAsBoolean();

            if (isStaticSchedule) {
                // Handle static schedule
                return program;
            } else {
                if (isWithinTimeRange(currentTime, startTime, endTime)) {
                    return program;
                }
            }
        }
        return null;
    }

    private boolean isWithinTimeRange(String currentTime, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date now = sdf.parse(currentTime);
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);

            return now.after(start) && now.before(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void handleLiveTVClick(JsonObject jsonObject, int position) {
        Intent intent = new Intent(mContext, LiveTVPlayer.class);
        intent.putExtra("contentID", mData.get(position).getID());
        intent.putExtra("source", mData.get(position).getStream_type());
        intent.putExtra("url", mData.get(position).getUrl());
        intent.putExtra("content_type", mData.get(position).getContent_type());
        intent.putExtra("name", mData.get(position).getName());
        intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
        intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
        intent.putExtra("userAgentLiveTV", jsonObject.get("user_agent").getAsString());
        intent.putExtra("refererTV", jsonObject.get("referer").isJsonNull() ? "" : jsonObject.get("referer").getAsString());
        intent.putExtra("cookieTV", jsonObject.get("cookie").isJsonNull() ? "" : jsonObject.get("cookie").getAsString());
        intent.putExtra("headersTV", jsonObject.get("headers").getAsString());
        mContext.startActivity(intent);
    }
}
