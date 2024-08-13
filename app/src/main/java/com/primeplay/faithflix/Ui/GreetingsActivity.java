package com.primeplay.faithflix.Ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primeplay.faithflix.BuildConfig;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.ServerError;
import com.primeplay.faithflix.Utils;
import com.primeplay.faithflix.util.AppConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;


public class GreetingsActivity extends BaseActivity {
    Context context;
    String userData;
    public static String notificationData = "";
    String apiKey;
    Integer loginMandatory;
    Integer maintenance;
    String blocked_regions;
    String latestAPKVersionName;
    String latestAPKVersionCode;
    String apkFileUrl;
    String whatsNewOnLatestApk;
    int updateSkipable;
    int updateType;
    int googleplayAppUpdateType;
    boolean pinLockStatus = false;
    String pinLockPin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        setContentView(R.layout.activity_greetings);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                AppConfig.rawUrl = mFirebaseRemoteConfig.getString("SERVER_URL");
                AppConfig.url = AppConfig.rawUrl + "android/";
                AppConfig.apiKey = mFirebaseRemoteConfig.getString("API_KEY");
                AppConfig.allowRoot = false;
                loadUserData();
            } else {
                Intent intent = new Intent(this, ServerError.class);
                startActivity(intent);
            }
        });


    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userData = sharedPreferences.getString("UserData", null);
        loadConfig();
    }

    private void loadConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "get_config", response -> {
            JsonObject jsonObjectJWT = new Gson().fromJson(response, JsonObject.class);
            String token = jsonObjectJWT.get("token").getAsString();
            try {
                Algorithm algorithm = Algorithm.HMAC256(AppConfig.apiKey);
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT jwt = JWT.decode(token);

                String config = jwt.getClaim("config").toString();
                Log.d("Mysecrets",config);
                JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
                apiKey = jsonObject.get("api_key").getAsString();
                loginMandatory = jsonObject.get("login_mandatory").getAsInt();
                maintenance = jsonObject.get("maintenance").getAsInt();
                blocked_regions = jsonObject.get("blocked_regions").isJsonNull() ? "" : jsonObject.get("blocked_regions").getAsString();

                saveConfig(config);
//                saveNotification();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                latestAPKVersionName = jsonObject.get("Latest_APK_Version_Name").getAsString();
                latestAPKVersionCode = jsonObject.get("Latest_APK_Version_Code").getAsString();
                apkFileUrl = jsonObject.get("APK_File_URL").getAsString();
                whatsNewOnLatestApk = jsonObject.get("Whats_new_on_latest_APK").getAsString();
                updateSkipable = jsonObject.get("Update_Skipable").getAsInt();
                updateType = jsonObject.get("Update_Type").getAsInt();
                googleplayAppUpdateType = jsonObject.get("googleplayAppUpdateType").getAsInt();
                openApp();
            } catch (JWTVerificationException exception) {

            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
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

        sr.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void openApp() {
        if (userData == null) {
            mainAppOpen();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(GreetingsActivity.this::verifyUser, 500);
        }
    }

    void verifyUser()
    {
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
        String email = jsonObject.get("Email").getAsString();
        String password = jsonObject.get("Password").getAsString();

        String originalInput = "login:" + email + ":" + password;
        String encoded = Utils.toBase64(originalInput);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "authentication", response -> {
            if (!response.equals("")) {
                JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
                String status = jsonObject1.get("Status").toString();
                status = status.substring(1, status.length() - 1);

                if (status.equals("Successful")) {
                    saveData(response);

                    JsonObject subObj = new Gson().fromJson(response, JsonObject.class);
                    int subscriptionType = subObj.get("subscription_type").getAsInt();
//                    saveUserSubscriptionDetails(subscriptionType);
//
//                    setOneSignalExternalID(String.valueOf(subObj.get("ID").getAsInt()));
//
//                    saveNotification();
                    Intent intent = new Intent(GreetingsActivity.this, PrimeActivity.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else if (status.equals("Invalid Credential")) {
//                    deleteData();
                    if (loginMandatory == 0) {
//                        saveNotification();
                        Intent intent = new Intent(GreetingsActivity.this, PrimeActivity.class);
                        intent.putExtra("Notification_Data", notificationData);
                        startActivity(intent);
                        notificationData = "";
                        finish();
                    } else {
                        Intent intent = new Intent(GreetingsActivity.this, PrimeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
//                deleteData();
                if (loginMandatory == 0) {
//                    saveNotification();
                    Intent intent = new Intent(GreetingsActivity.this, PrimeActivity.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else {
                    Intent intent = new Intent(GreetingsActivity.this, LoginSignup.class);
                    startActivity(intent);
                    finish();
                }
            }

        }, error -> {
            // Do nothing because
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }

            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("encoded", encoded);
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void saveConfig(String config) throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", config);
        editor.apply();


        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        if (!jsonObject.get("pinLockStatus").isJsonNull()) {
            if (jsonObject.get("pinLockStatus").getAsInt() == 1) {
                pinLockStatus = true;
            } else {
                pinLockStatus = false;
            }
        }
        pinLockPin = jsonObject.get("pinLockPin").isJsonNull() ? "" : jsonObject.get("pinLockPin").getAsString();
        AppConfig.bGljZW5zZV9jb2Rl = jsonObject.get("license_code").isJsonNull() ? "" : jsonObject.get("license_code").getAsString();
        if (jsonObject.get("safeMode").getAsInt() == 1) {
            String safeModeVersions = jsonObject.get("safeModeVersions").isJsonNull() ? "" : jsonObject.get("safeModeVersions").getAsString();
            if (!safeModeVersions.equals("")) {
                String[] safeModeVersionsArrey = safeModeVersions.split(",");
                for (String safeModeVersion : safeModeVersionsArrey) {
                    if (BuildConfig.VERSION_NAME.equals(safeModeVersion.trim())) {
                        AppConfig.safeMode = true;
                    }
                }
            } else {
                AppConfig.safeMode = true;
            }
        }

        loadRemoteConfig();
    }

    private void loadRemoteConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Utils.fromBase64("aHR0cHM6Ly9jbG91ZC50ZWFtLWRvb28uY29tL0Rvb28vYXBpL2dldENvbmZpZy5waHA/Y29kZT0=") + AppConfig.bGljZW5zZV9jb2Rl, response -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RemoteConfig", String.valueOf(response));
            editor.apply();
        }, error -> {
            // Do nothing because
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void saveData(String userData) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", userData);
        editor.apply();
    }

    void mainAppOpen() {
        if (loginMandatory == 0) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
//                saveNotification();
                Intent intent = new Intent(GreetingsActivity.this, PrimeActivity.class);
                intent.putExtra("Notification_Data", notificationData);
                startActivity(intent);
                notificationData = "";
                finish();
            }, 500);
        } else if (loginMandatory == 1) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
//                saveNotification();
                Intent intent = new Intent(GreetingsActivity.this, LoginSignup.class);
                startActivity(intent);
                finish();
            }, 500);
        }
    }
}