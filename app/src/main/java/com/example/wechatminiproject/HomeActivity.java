package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String division = "";
    String year = "";
    String branch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        sharedPreferences = this.getSharedPreferences("com.example.wechatminiproject", Context.MODE_PRIVATE);

        boolean isNewUser = sharedPreferences.getBoolean("isNewUser", false);

        ParseQuery<ParseObject> currentStudent = new ParseQuery<ParseObject>("Students");
        currentStudent.whereEqualTo("Username",ParseUser.getCurrentUser().getUsername());

        currentStudent.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    for(ParseObject object : objects)
                    {
                        division = object.get("Division").toString();
                        year = object.get("Year").toString();
                        branch = object.get("Branch").toString();
                    }
                }
            }
        });
    }

    public void logOut(View view)
    {
        ParseUser.logOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void goToChats(View view)
    {

        Intent intent = new Intent(HomeActivity.this, ChatsActivity.class);
        intent.putExtra("division",division);
        intent.putExtra("year",year);
        intent.putExtra("branch",branch);
        startActivity(intent);

    }

    public void goToGroupChats(View view)
    {
        Intent intent = new Intent(HomeActivity.this, GroupChatsActivity.class);
        intent.putExtra("division",division);
        intent.putExtra("year",year);
        intent.putExtra("branch",branch);
        startActivity(intent);
    }
}