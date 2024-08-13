package com.primeplay.faithflix.fragmentsUi;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.primeplay.faithflix.HelperUtils;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.adapter.ImageSliderAdepter;
import com.primeplay.faithflix.adapter.LiveTvChannelListAdepter;
import com.primeplay.faithflix.adapter.MovieListAdepter;
import com.primeplay.faithflix.adapter.ThumbnailAdapter;
import com.primeplay.faithflix.adapter.TrendingListAdepter;
import com.primeplay.faithflix.adapter.moviesOnlyForYouListAdepter;
import com.primeplay.faithflix.models.ImageSliderItem;
import com.primeplay.faithflix.models.LiveTvChannelList;
import com.primeplay.faithflix.models.MovieList;
import com.primeplay.faithflix.models.TrendingList;
import com.primeplay.faithflix.sharedpref.ConfigManager;
import com.primeplay.faithflix.sharedpref.UserManager;
import com.primeplay.faithflix.util.AppConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.vejei.viewpagerindicator.indicator.CircleIndicator;
import io.github.vejei.viewpagerindicator.indicator.RectIndicator;

public class HomeFragment extends Fragment {
    String imageSliderType;
    View view;
    ViewPager2 ViewpagerHome;
    int movieImageSliderMaxVisible;
    int userID, shuffleContents;
    Context mContext;
    RecyclerView thumbnailRecyclerview, topTenMoviesRecyclerview, RecentlyReleasedMoviesRecyclerview,NowOnTV_recyclerview,forYouRecyclerview;
    List<ImageSliderItem> imageSliderItems;
    ImageSliderAdepter imageSliderAdepter;
    ThumbnailAdapter thumbnailAdapter;
    RectIndicator circleIndicator;
    TextView monthTextView;
    private ImageView thumbnailImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewpagerHome = view.findViewById(R.id.ViewpagerHome);
        transparentStatusAndNavigation();
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        try {
            JSONObject userObject = UserManager.loadUser(mContext);
            userID = userObject.getInt("ID");

        } catch (Exception e) {

        }

        try {
            JSONObject configObject = ConfigManager.loadConfig(mContext);
            imageSliderType = configObject.getString("image_slider_type");
            movieImageSliderMaxVisible = configObject.getInt("movie_image_slider_max_visible");
            shuffleContents = configObject.getInt("shuffle_contents");
        } catch (Exception e) {

        }

        thumbnailRecyclerview = view.findViewById(R.id.thumbnailRecyclerview);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        topTenMoviesRecyclerview = view.findViewById(R.id.topTenMoviesRecyclerview);
        HelperUtils helperUtils = new HelperUtils(requireActivity());
        loadHomeViewpager();

        imageSliderItems = new ArrayList<>();
        ViewpagerHome.setClipToPadding(false);
        ViewpagerHome.setClipChildren(false);
        ViewpagerHome.setOffscreenPageLimit(3);
        ViewpagerHome.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        ViewpagerHome.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                thumbnailRecyclerview.scrollToPosition(position);
                thumbnailAdapter.updateSelectedPosition(position);

            }
        });
        monthTextView = view.findViewById(R.id.monthTextView);
        RecentlyReleasedMoviesRecyclerview = view.findViewById(R.id.RecentlyReleasedMoviesRecyclerview);
        NowOnTV_recyclerview = view.findViewById(R.id.NowOnTV_recyclerview);
        forYouRecyclerview = view.findViewById(R.id.forYouRecyclerview);
        setMonthName();
        LoadHomeContent();


        return view;
    }

    @SuppressLint("HardwareIds")
    private void LoadHomeContent() {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest sr3 = new StringRequest(Request.Method.GET, AppConfig.url + "getRecentContentList/Movies", response ->
        {
            Log.d("movi_res",response);
            if (!response.equals("No Data Avaliable"))
            {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> recentlyAddedMovieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {

                        year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();


                    if (status == 1) {
                        recentlyAddedMovieList.add(new MovieList(id, type, name, year, poster, "", "", ""));
                    }
                }

                if (shuffleContents == 1) {
                    Collections.shuffle(recentlyAddedMovieList);
                }

                MovieListAdepter myadepter = new MovieListAdepter(mContext, recentlyAddedMovieList);
                RecentlyReleasedMoviesRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                RecentlyReleasedMoviesRecyclerview.setAdapter(myadepter);

            } else {

            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr3);
        StringRequest sr5 = new StringRequest(Request.Method.GET, AppConfig.url + "getFeaturedLiveTV", response -> {
            Log.d("responses",response);
            if (!response.equals("No Data Avaliable")) {

                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<LiveTvChannelList> liveTVChannelList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String banner = rootObject.get("banner").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    String streamType = rootObject.get("stream_type").getAsString();
                    String url = rootObject.get("url").getAsString();
                    int contentType = rootObject.get("content_type").getAsInt();
                    String drm_uuid = rootObject.get("drm_uuid").isJsonNull() ? "" : rootObject.get("drm_uuid").getAsString();
                    String drm_license_uri = rootObject.get("drm_license_uri").isJsonNull() ? "" : rootObject.get("drm_license_uri").getAsString();

                    if (status == 1) {
                        liveTVChannelList.add(new LiveTvChannelList(id, name, banner, streamType, url, contentType, type, true, drm_uuid, drm_license_uri));
                    }
                }


                if (shuffleContents == 1) {
                    Collections.shuffle(liveTVChannelList);
                }

                LiveTvChannelListAdepter myadepter = new LiveTvChannelListAdepter(requireContext(), liveTVChannelList);
                NowOnTV_recyclerview.setLayoutManager(new GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false));
                NowOnTV_recyclerview.setAdapter(myadepter);

                // Fetch the program list for each channel


            } else {

            }
        }, error -> {
            // Handle the error
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr5);

        StringRequest sr12 = new StringRequest(Request.Method.GET, AppConfig.url + "getTrending", response -> {

            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<TrendingList> trendingList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int content_type = rootObject.get("content_type").getAsInt();


                    trendingList.add(new TrendingList(id, type, content_type, poster, "", "", ""));
                }

                TrendingListAdepter myadepter = new TrendingListAdepter(mContext, trendingList);
                topTenMoviesRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                topTenMoviesRecyclerview.setAdapter(myadepter);


            } else {

            }
        }, error -> {
            Log.d("test", error.toString());
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr12);
        String tempUserID = null;
        if (userID != 0) {
            tempUserID = String.valueOf(userID);
        } else {
            tempUserID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        StringRequest sr6 = new StringRequest(Request.Method.GET, AppConfig.url + "beacauseYouWatched/Movies/" + tempUserID + "/10", response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        movieList.add(new MovieList(id, type, name, year, poster,"","",""));
                    }
                }

                Collections.shuffle(movieList);

                moviesOnlyForYouListAdepter myadepter = new moviesOnlyForYouListAdepter(mContext, movieList);
                forYouRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                forYouRecyclerview.setAdapter(myadepter);

            } else {

            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr6);
    }

    private void transparentStatusAndNavigation() {
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = requireActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void loadHomeViewpager() {
        if (movieImageSliderMaxVisible > 0) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            @SuppressLint("NotifyDataSetChanged") StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getMovieImageSlider", response -> {
                if (!response.equals("No Data Avaliable")) {

                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    int i = 0;
                    int maxVisible = movieImageSliderMaxVisible;
                    for (JsonElement r : jsonArray) {
                        if (i < maxVisible) {
                            JsonObject rootObject = r.getAsJsonObject();
                            int id = rootObject.get("id").getAsInt();
                            String name = rootObject.get("name").getAsString();
                            String banner = rootObject.get("banner").getAsString();
                            String poster = rootObject.get("poster").getAsString();
                            String logo = rootObject.has("logo_image") ? rootObject.get("logo_image").getAsString() : "";
                            int status = rootObject.get("status").getAsInt();
                            if (status == 1) {
                                imageSliderItems.add(new ImageSliderItem(banner, name, poster, 0, id, null, logo, "", "", ""));
                                i++;
                            }
                        }
                    }

                    ViewpagerHome.setVisibility(View.VISIBLE);
                    imageSliderAdepter = new ImageSliderAdepter(imageSliderItems, ViewpagerHome, userID);
                    ViewpagerHome.setAdapter(imageSliderAdepter);
                    imageSliderAdepter.notifyDataSetChanged();
                    circleIndicator.setWithViewPager2(ViewpagerHome, false);
                    circleIndicator.setItemCount(5);
                    circleIndicator.setAnimationMode(RectIndicator.AnimationMode.SLIDE);

                    thumbnailAdapter = new ThumbnailAdapter(imageSliderItems, ViewpagerHome);
                    thumbnailRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                    thumbnailRecyclerview.setAdapter(thumbnailAdapter);
                } else {
                    ViewpagerHome.setVisibility(View.GONE);
                }
            }, error -> {
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        } else {
            ViewpagerHome.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    private void setMonthName() {
        Calendar calendar = Calendar.getInstance();
        int monthIndex = calendar.get(Calendar.MONTH); // Gets the current month index (0 = January, 11 = December)

        // Get month names
        String[] months = new DateFormatSymbols().getMonths();

        // Set the month name to TextView
        String currentMonth = months[monthIndex];
        monthTextView.setText("Top Ten Movies to Watch in " + currentMonth);
    }


}