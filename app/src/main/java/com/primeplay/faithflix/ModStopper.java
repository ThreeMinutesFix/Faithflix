package com.primeplay.faithflix;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class ModStopper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mod_stopper);
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Modder Alert?")
                .setMessage("Look Like Your using some Mod Version of our Apk Or trying to tamper it ")
                .setCancelable(false)
                .setPositiveButton("Sorry", R.drawable.baseline_delete_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                        finishAndRemoveTask();
                        finishAffinity();
                        Toast.makeText(ModStopper.this, "Thankyou for not using our app", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.baseline_close_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Toast.makeText(ModStopper.this, "So you gonna look the progressbar forever!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }
}