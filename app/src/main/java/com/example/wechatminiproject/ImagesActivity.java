package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class ImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        Objects.requireNonNull(getSupportActionBar()).hide();

    }

    public void sendDMSImages(View view)
    {
        Intent intent = new Intent(ImagesActivity.this, ChatsActivity.class);
        intent.putExtra("division",getIntent().getStringExtra("division"));
        intent.putExtra("year",getIntent().getStringExtra("year"));
        intent.putExtra("branch",getIntent().getStringExtra("branch"));
        intent.putExtra("Code",2);
        startActivity(intent);
    }

    public void sendGroupsImages(View view)
    {
        Intent intent = new Intent(ImagesActivity.this, GroupChatsActivity.class);
        intent.putExtra("division",getIntent().getStringExtra("division"));
        intent.putExtra("year",getIntent().getStringExtra("year"));
        intent.putExtra("branch",getIntent().getStringExtra("branch"));
        intent.putExtra("CODE",20);
        startActivity(intent);
    }
}