package com.primeplay.faithflix.Ui;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.CaptionStyleCompat;
import androidx.media3.ui.PlayerView;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primeplay.faithflix.HelperUtils;
import com.primeplay.faithflix.OnTimeClickListener;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.adapter.ProgramAdapter;
import com.primeplay.faithflix.adapter.TimeIntervalAdapter;
import com.primeplay.faithflix.models.Program;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory;

@UnstableApi
public class LiveTVPlayer extends BaseActivity implements OnTimeClickListener {
    PlayerView playerView;
    RecyclerView schedulerrecycleview, schedule_recyclerview_land;
    Context context = this;
    NestedScrollView recyclerviewscroller;
    private ExoPlayer simpleExoPlayer;
    RecyclerView timecontainer;
    ProgramAdapter programAdapter;
    String preferredLanguage = "en";
    private DefaultTrackSelector trackSelector;
    View mylayout;
    LinearLayout outlineLayout;
    private MediaRouteButton mediaRouteButton;
    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    long playbackPosition;
    boolean playWhenReady;
    String userData = null;
    int userId;
    OnBackPressedDispatcher onBackPressedDispatcher;
    int sourceID;
    PowerManager.WakeLock wakeLock;
    TextView channel_name;
    long pauseposition = 0;
    String source;
    int contentID;
    String cpUrl = "";
    Map<String, String> defaultRequestProperties = new HashMap<>();
    String userAgent = "";
    android.webkit.WebView webView;
    String DrmUuid = "";
    String DrmLicenseUri = "";
    View mDecorView;
    ImageView tvprograms, closeitems;
    private LinearLayout scheduler_layout;
    MaterialButton skipLive;
    ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300 * 60 * 1000L /*300 minutes*/);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        transparentStatusAndNavigation();
        setStatusBar();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_live_tvplayer);
        playerView = findViewById(R.id.playerView);
        main = findViewById(R.id.main);
        schedulerrecycleview = findViewById(R.id.schedule_recyclerview);
        loadConfig();
        loadData();
        recyclerviewscroller = findViewById(R.id.recyclerviewscroller);
        Intent intent = getIntent();
        contentID = Objects.requireNonNull(intent.getExtras()).getInt("contentID");
        sourceID = intent.getExtras().getInt("SourceID");
        String name = intent.getExtras().getString("name");
        source = intent.getExtras().getString("source");
        cpUrl = intent.getExtras().getString("url");
        DrmUuid = intent.getExtras().getString("DrmUuid");
        DrmLicenseUri = intent.getExtras().getString("DrmLicenseUri");
        channel_name = findViewById(R.id.channel_name);
        channel_name.setText(name);
        tvprograms = playerView.findViewById(R.id.tvprograms);
        mediaRouteButton = playerView.findViewById(R.id.media_route_button);
        // Create a MediaRouteSelector to specify the types of routes to support
        mediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .build();

        mediaRouter = MediaRouter.getInstance(this);
        schedule_recyclerview_land = playerView.findViewById(R.id.schedule_recyclerview_land);

        // Set the media route selector on the MediaRouteButton
        mediaRouteButton.setRouteSelector(mediaRouteSelector);
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, "MyMediaSession");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                // Start playback
                simpleExoPlayer.play();
            }

            @Override
            public void onPause() {
                super.onPause();
                // Pause playback
                simpleExoPlayer.pause();
            }

            // Other media control methods as needed
        });
        skipLive = playerView.findViewById(R.id.skipLive);
        loadLivetvData();
        loadScheduler(name);
        loadLiveTVstream(cpUrl, source);
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playback_position", 0);
            playWhenReady = savedInstanceState.getBoolean("play_when_ready", true);
            if (simpleExoPlayer != null) {
                simpleExoPlayer.seekTo(playbackPosition);
                simpleExoPlayer.setPlayWhenReady(playWhenReady);
                simpleExoPlayer.play();
            } else {
                simpleExoPlayer.seekTo(0);
                simpleExoPlayer.play();
            }
        }
        scheduler_layout = playerView.findViewById(R.id.scheduler_layout);

        mylayout = playerView.findViewById(R.id.mylayout);
        outlineLayout = mylayout.findViewById(R.id.outlineLayout);
        closeitems = mylayout.findViewById(R.id.closeitems);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // The device is in landscape mode
            tvprograms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openWithAnimation();
                }
            });
            outlineLayout.setOnClickListener(view ->
            {
                closeWithAnimation();
            });
            closeitems.setOnClickListener(view ->
            {
                closeWithAnimation();
            });
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mylayout.setVisibility(View.GONE);
        }


        timecontainer = mylayout.findViewById(R.id.timecontainer);
        timecontainer.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> timeIntervals = HelperUtils.generateTimePoints();
        TimeIntervalAdapter myadepter = new TimeIntervalAdapter(this, timeIntervals,this);
        timecontainer.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false));
        timecontainer.setAdapter(myadepter);
        myadepter.scrollToCurrentTime(timecontainer);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                isBackPressed = true;
                releasePlayer();
                finish();
            }
        });


    }

    private void openWithAnimation() {
        scheduler_layout.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(scheduler_layout, "translationX", scheduler_layout.getWidth(), 0f);
        animator.setDuration(300); // Duration of the animation
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Smooth interpolation

        // Start the animation
        animator.start();

    }

    private void closeWithAnimation() {
        // Create an ObjectAnimator for sliding out the layout to the right
        ObjectAnimator animator = ObjectAnimator.ofFloat(scheduler_layout, "translationX", 0f, scheduler_layout.getWidth());
        animator.setDuration(300); // Duration of the animation
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Smooth interpolation
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                scheduler_layout.setVisibility(View.GONE); // Hide the layout
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        // Start the animation
        animator.start();
    }

    private void loadLiveTVstream(String cpUrl, String source) {
        Log.d("test", defaultRequestProperties.toString());
        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
        if (source.equalsIgnoreCase("m3u8")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .setUri(cpUrl)
                    .build();
            MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem);
            initializePlayer(hlsMediaSource);
        } else if (source.equalsIgnoreCase("dash")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setUri(cpUrl)
                    .build();
            MediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(dashMediaSource);
        }

    }

    private void initializePlayer(MediaSource mediaSource) {
        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(LiveTVPlayer.this, videoTrackSelectionFactory);
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage(preferredLanguage));
        NextRenderersFactory renderersFactory = new NextRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(NextRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        simpleExoPlayer = new ExoPlayer.Builder(this, renderersFactory).setTrackSelector(trackSelector).setSeekForwardIncrementMs(10000).setSeekBackIncrementMs(10000).build();
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        Objects.requireNonNull(playerView.getSubtitleView()).setApplyEmbeddedStyles(false);
        playerView.getSubtitleView().setApplyEmbeddedFontSizes(false);
        playerView.getSubtitleView().setStyle(new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, null));
        playerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        playerView.getSubtitleView().setBottomPaddingFraction(0.1f);
        simpleExoPlayer.setMediaSource(mediaSource);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        pauseposition = simpleExoPlayer.getCurrentPosition();
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                handlePlayerError((ExoPlaybackException) error);
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
                if (!playWhenReady) {
                    skipLive.setVisibility(View.VISIBLE);
                    animateButtonColor(skipLive, Color.TRANSPARENT, R.color.btn_color_2);
                    skipLive.setText("Skip To Live");
                    skipLive.setOnClickListener(view ->
                    {
                        skipLive.setText("Skipping to live");
                        simpleExoPlayer.seekTo(0);
                        simpleExoPlayer.setPlayWhenReady(true);
                        simpleExoPlayer.play();
                    });
                } else {
                    if (playbackPosition >= 1) {
                        skipLive.setText("Skip To Live");
                        skipLive.setVisibility(View.GONE);
                        animateButtonColor(skipLive, R.color.btn_color_2, Color.TRANSPARENT);
                    } else if (pauseposition >= 2) {
                        skipLive.setText("Skip To Live");
                        skipLive.setVisibility(View.VISIBLE);
                        animateButtonColor(skipLive, R.color.btn_color_2, Color.TRANSPARENT);
                    }
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void animateButtonColor(MaterialButton skipLive, int startColor, int endColor) {
        ValueAnimator colorAnimation = ObjectAnimator.ofArgb(startColor, endColor);
        colorAnimation.addUpdateListener(animator -> {
            skipLive.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));

        });
        colorAnimation.setDuration(300);
        colorAnimation.start();

    }

    private void loadLivetvData() {

        Intent intent = getIntent();
        if (!Objects.equals(intent.getExtras().getString("userAgentLiveTV"), "")) {
            userAgent = intent.getExtras().getString("userAgentLiveTV");
        } else {
            userAgent = WebSettings.getDefaultUserAgent(this);
        }

        if (!Objects.equals(intent.getExtras().getString("refererTV"), "")) {
            defaultRequestProperties.put("Referer", intent.getExtras().getString("refererTV"));
        }

        if (!Objects.equals(intent.getExtras().getString("cookieTV"), "")) {
            defaultRequestProperties.put("Cookie", intent.getExtras().getString("cookieTV"));
        }

        if (!Objects.equals(intent.getExtras().getString("headersTV"), "")) {
            JsonArray headers = new Gson().fromJson(intent.getExtras().getString("headersTV"), JsonArray.class);
            for (JsonElement headerElement : headers) {
                JsonObject headerObject = headerElement.getAsJsonObject();
                String[] header = headerObject.get("header").getAsString().split(":", 2);
                if (header.length == 2) {
                    defaultRequestProperties.put(header[0], header[1]);
                }
            }
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);

    }


    private void loadScheduler(String channelName) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://tvschedules.threeminutesfix.in/api.php?channelname=" + Uri.encode(channelName) + "&apikey=1a6ba61a2f2f8f1ff0d69b787a76c520";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Program> programs = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String todayDate = sdf.format(new Date());
                            boolean hasTodayPrograms = false;

                            // Using AtomicInteger for currentPosition
                            AtomicInteger currentPosition = new AtomicInteger(-1);

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject programObject = response.getJSONObject(i);

                                String programName = programObject.getString("program_name");
                                String startTime = programObject.getString("start_time");
                                String endTime = programObject.getString("end_time");
                                String imageUrl = programObject.optString("image_url", "");
                                String programDate = programObject.getString("date");
                                int isStaticSchedule = programObject.getInt("is_static_schedule");

                                if (todayDate.equals(programDate)) {
                                    programs.add(new Program(programName, startTime, imageUrl));
                                    hasTodayPrograms = true;

                                    // Check if the current time is within the program's time range
                                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                    if (currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) < 0) {
                                        currentPosition.set(programs.size() - 1); // Update the position in the filtered list
                                    }
                                }
                            }

                            if (!hasTodayPrograms) {
                                fetchStaticPrograms(channelName, currentPosition);
                            } else {
                                setupRecyclerViews(programs, currentPosition.get());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SchedulerApp", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SchedulerApp", "No response or error: " + error.getMessage());
                    }
                });

        queue.add(jsonArrayRequest);
    }
    private void fetchStaticPrograms(String channelName, AtomicInteger currentPosition) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://tvschedules.threeminutesfix.in/api.php?channelname=" + Uri.encode(channelName) + "&apikey=1a6ba61a2f2f8f1ff0d69b787a76c520";

        JsonArrayRequest staticRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray staticResponse) {
                        try {
                            List<Program> staticPrograms = new ArrayList<>();
                            long currentTimeMillis = System.currentTimeMillis(); // Get current time in milliseconds

                            for (int i = 0; i < staticResponse.length(); i++) {
                                JSONObject programObject = staticResponse.getJSONObject(i);
                                String programName = programObject.getString("program_name");
                                String startTime = programObject.getString("start_time");
                                String endTime = programObject.getString("end_time");
                                String imageUrl = programObject.optString("image_url", "");

                                // Convert start and end times to milliseconds
                                long programStartTime = parseTimeToMillis(startTime);
                                long programEndTime = parseTimeToMillis(endTime);

                                Program program = new Program(programName, startTime, imageUrl);
                                staticPrograms.add(program);

                                // Check if current time is within the program's time range
                                if (currentTimeMillis >= programStartTime && currentTimeMillis <= programEndTime) {
                                    currentPosition.set(staticPrograms.size() - 1); // Current position in the list

                                    // Set the adapter and scroll to the position for both RecyclerViews
                                    setupRecyclerViews(staticPrograms, currentPosition.get());
                                    break; // Exit loop after finding the matching program
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SchedulerApp", "Error parsing static schedule JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SchedulerApp", "Static schedule request error: " + error.getMessage());
                    }
                });

        queue.add(staticRequest);
    }

    private void setupRecyclerViews(List<Program> programs, int currentPosition) {
        // Set up RecyclerViews
        programAdapter = new ProgramAdapter(programs, this, currentPosition);

        schedulerrecycleview.setLayoutManager(new LinearLayoutManager(this));
        schedulerrecycleview.setAdapter(programAdapter);
        schedule_recyclerview_land.setLayoutManager(new LinearLayoutManager(this));
        schedule_recyclerview_land.setAdapter(programAdapter);
        scrollToPosition(schedulerrecycleview, currentPosition);
        scrollToPosition(schedule_recyclerview_land, currentPosition);
    }

    private void scrollToPosition(RecyclerView recyclerView, int position) {
        recyclerView.post(() -> {
            if (position != -1) {
                recyclerView.scrollToPosition(position);
                NestedScrollView nestedScrollView = findViewById(R.id.recyclerviewscroller);
                nestedScrollView.post(() -> {
                    nestedScrollView.smoothScrollTo(0, recyclerView.getTop());
                });
            }
        });
    }
    private long parseTimeToMillis(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = format.parse(time);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (simpleExoPlayer != null) {
            outState.putLong("playback_position", simpleExoPlayer.getCurrentPosition());
            outState.putBoolean("play_when_ready", simpleExoPlayer.getPlayWhenReady());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer.getCurrentPosition();
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer.clearVideoSurface();
            simpleExoPlayer = null;
        }
    }

    Boolean isBackPressed = false;

    private String generateErrorCode(int errorType) {
        // Base code
        final String baseCode = "FFPE-";

        // Error code mapping
        switch (errorType) {
            case ExoPlaybackException.TYPE_SOURCE:
                return baseCode + "7701"; // Example: Source error
            case ExoPlaybackException.TYPE_RENDERER:
                return baseCode + "7702"; // Example: Renderer error
            case ExoPlaybackException.TYPE_UNEXPECTED:
                return baseCode + "7703"; // Example: Unexpected error
            case ExoPlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW:
                return baseCode + "7704"; // Example: Behind live window
            case ExoPlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS:
                return baseCode + "7705"; // Example: Bad HTTP status
            default:
                return baseCode + "7700"; // Unknown error
        }
    }

    private void handlePlayerError(ExoPlaybackException error) {
        // Determine the error message and code
        String errorMessage = "An unknown error occurred.";
        String errorCode = generateErrorCode(error.type);
        boolean showDialog = true;

        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            errorMessage = "Source error: " + error.getMessage();
        } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
            errorMessage = "Renderer error: " + error.getMessage();
        } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
            errorMessage = "Unexpected error: " + error.getMessage();
        } else if (error.type == ExoPlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
            // Handle BEHIND_LIVE_WINDOW error automatically
            simpleExoPlayer.seekTo(0);
            showDialog = false;
        } else if (error.type == ExoPlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
            errorMessage = "Error: Bad HTTP status - " + error.getMessage();
        }

        // Show dialog for errors other than BEHIND_LIVE_WINDOW
        if (showDialog) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Error Occurred")
                    .setMessage(errorMessage + "\nError Code: " + errorCode)
                    .setPositiveButton("Retry", (dialog, which) -> {
                        // Retry action: reset playback
                        simpleExoPlayer.seekTo(0);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isBackPressed) {
            pausePlayer();
        }

    }

    private void pausePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();
            simpleExoPlayer.pause();
        }
    }

    private void transparentStatusAndNavigation() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            long IMMERSIVE_FLAG_TIMEOUT = 500L;
            int FLAGS_FULLSCREEN = (View.SYSTEM_UI_FLAG_LOW_PROFILE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            main.postDelayed(() -> main.setSystemUiVisibility(FLAGS_FULLSCREEN),
                    IMMERSIVE_FLAG_TIMEOUT);
        }
    }

    @Override
    public void onTimeClick(String timePoint) {
        int position = findProgramPositionByTime(timePoint);
        if (position != -1) {
            timecontainer.scrollToPosition(position);
        }
    }
    private int findProgramPositionByTime(String timePoint) {
        // Implement logic to map timePoint to a program position
        // Example: Iterate over program list and find matching timePoint
        for (int i = 0; i < programAdapter.getItemCount(); i++) {
            Program program = programAdapter.getProgramList().get(i);
            if (program.getProgramTime().equals(timePoint)) {
                return i;
            }
        }
        return -1;
    }
}