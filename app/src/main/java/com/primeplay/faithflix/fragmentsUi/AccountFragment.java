package com.primeplay.faithflix.fragmentsUi;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.primeplay.faithflix.R;
import com.primeplay.faithflix.sharedpref.UserManager;

import org.json.JSONObject;

import java.io.File;

public class AccountFragment extends Fragment {
    View view;
    int userID;
    TextView AppUserId;
    LinearLayout appCache;
    Context mContext;
    String tempUserID = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        appCache = view.findViewById(R.id.appCache);
        try {
            JSONObject userObject = UserManager.loadUser(mContext);
            userID = userObject.getInt("ID");

        } catch (Exception e) {

        }
        appCache.setOnClickListener(view1 ->
        {
            clearCache();
        });
        AppUserId  = view.findViewById(R.id.UserId);

        if (userID != 0) {
            tempUserID = String.valueOf(userID);
            AppUserId.setText(tempUserID);
        } else {
            tempUserID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            AppUserId.setText(tempUserID);
        }

        return view;
    }

    private void clearCache() {
        try {
            File cacheDir = requireContext().getCacheDir(); // Get the cache directory
            deleteDir(cacheDir); // Delete the cache directory and its contents
            Toast.makeText(requireContext(), "Cache cleared successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to clear cache", Toast.LENGTH_SHORT).show();
        }
    }

    // Recursive method to delete all files in a directory
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }
}