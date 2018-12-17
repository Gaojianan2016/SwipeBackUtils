package com.gjn.swipebackutils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gjn.swipeback.SwipeHelper;

public class BackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);


        SwipeHelper swipeHelper = new SwipeHelper(this);
        swipeHelper.translucentWindowsBackground();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackActivity.this, BackActivity.class));
            }
        });
    }
}
