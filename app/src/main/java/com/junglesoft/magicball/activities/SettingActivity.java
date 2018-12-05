package com.junglesoft.magicball.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.junglesoft.magicball.R;

public class SettingActivity extends AppCompatActivity {
     TextView tvPrivacyPolicy;
     Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.setTitle("Setting");

        tvPrivacyPolicy = findViewById(R.id.privacy_policy);
        btnBack = findViewById(R.id.back_button);

        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app-privacy-policy-generator.firebaseapp.com/#"));
                startActivity(browserIntent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,MainActivity.class));
            }
        });
    }
}
