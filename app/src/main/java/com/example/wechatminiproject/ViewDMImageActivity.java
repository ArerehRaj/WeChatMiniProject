package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Objects;

public class ViewDMImageActivity extends AppCompatActivity {

    TextView message;
    ImageView imageSent;

    String imageMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_d_m_image);

        Objects.requireNonNull(getSupportActionBar()).hide();

        message = findViewById(R.id.imageMessageTextView);
        imageSent = findViewById(R.id.imageSended);

        imageMessage = getIntent().getStringExtra("message");

        Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("data"),0,getIntent().getByteArrayExtra("data").length);
        imageSent.setImageBitmap(bitmap);
        message.setText(imageMessage);

    }
}