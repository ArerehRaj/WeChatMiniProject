package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        getSupportActionBar().hide();

        String branch = getIntent().getStringExtra("branch");
        String year = getIntent().getStringExtra("year");
        String division = getIntent().getStringExtra("division");

        Toast.makeText(this,"Branch: " + branch + " Year: " + year + " Division: " + division,Toast.LENGTH_SHORT).show();
    }
}