package com.hadjmohamed.oran_agro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LanguageDeliveryBoyAcitvity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_delivery_boy);

        // ToolBar
        toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اللغة");
        btnGoBack = findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (btnGoBack == view){
            startActivity(new Intent(LanguageDeliveryBoyAcitvity.this,
                    AccountPageDeliveryBoyActivity.class));
            finish();
        }
    }
}