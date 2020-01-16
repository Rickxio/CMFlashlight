package com.dema.versatile.mediation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdActivity extends AppCompatActivity {
    private static AdActivity sAdActivity = null;

    public static AdActivity getInstance() {
        return sAdActivity;
    }

    public static void start(Context context) {
        try {
            Intent intent = new Intent(context, AdActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.dema.versatile.lib.R.layout.activity_empty);
        sAdActivity = this;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
