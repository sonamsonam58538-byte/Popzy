package com.example.popzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class loadingaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loadinga);

        // 3 seconds ke baad MainActivity open hogi
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(loadingaActivity.this, MainActivity.class);
            startActivity(intent);

            // Loading screen ko close kar do
            finish();

        }, 3000);
    }
}