package com.fdurainbow.rainbow_egg_client.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fdurainbow.rainbow_egg_client.R;

public class FollowActivity extends AppCompatActivity {

    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        iv_back = (ImageView)findViewById(R.id.follow_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FollowActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}