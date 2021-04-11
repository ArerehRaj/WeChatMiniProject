package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        sharedPreferences = this.getSharedPreferences("com.example.wechatminiproject", Context.MODE_PRIVATE);

        boolean isNewUser = sharedPreferences.getBoolean("isNewUser", false);

        Toast.makeText(this,"Is new User " + isNewUser, Toast.LENGTH_SHORT).show();
    }
}