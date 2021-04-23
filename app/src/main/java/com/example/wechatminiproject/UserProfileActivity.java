package com.example.wechatminiproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    TextView userFullName;
    TextView username;
    TextView userBranchName;
    TextView userYearValue;

    TextInputEditText inputFullName;
    TextInputEditText inputUserName;
    TextInputEditText inputEmail;
    TextInputEditText inputDivision;

    ImageView userProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();

        userFullName = findViewById(R.id.userFullName);
        username = findViewById(R.id.username);
        userBranchName = findViewById(R.id.userBranchName);
        userYearValue = findViewById(R.id.userYearValue);

        inputFullName = findViewById(R.id.inputFullName);
        inputUserName = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputUserEmail);
        inputDivision = findViewById(R.id.inputUserDivision);

        userProfilePicture = findViewById(R.id.userProfilePicture);

        Intent intent = getIntent();
        String division = intent.getStringExtra("division");
        String year = intent.getStringExtra("year");
        String branch = intent.getStringExtra("branch");
        String fullName = intent.getStringExtra("FullName");
        String email = intent.getStringExtra("Email");
        byte[] data = intent.getByteArrayExtra("data");

        userFullName.setText(fullName);
        username.setText(ParseUser.getCurrentUser().getUsername());
        userBranchName.setText(branch);
        userYearValue.setText(year);

        inputFullName.setText(fullName);
        inputUserName.setText(ParseUser.getCurrentUser().getUsername());
        inputDivision.setText(division);
        inputEmail.setText(email);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        userProfilePicture.setImageBitmap(bitmap);

    }

    public void updateProfile(View view)
    {
        new AlertDialog.Builder(UserProfileActivity.this)
                .setTitle("Update Profile Picture")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFullName = Objects.requireNonNull(inputFullName.getText()).toString();
                        String newDivision = Objects.requireNonNull(inputDivision.getText()).toString();
                        String newEmail = Objects.requireNonNull(inputEmail.getText()).toString();

                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
                        query.whereEqualTo("Username",ParseUser.getCurrentUser().getUsername());

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null)
                                {
                                    if(objects.size() > 0)
                                    {
                                        for(ParseObject object : objects)
                                        {
                                            object.put("FullName",newFullName);
                                            object.put("Division",newDivision);
                                            object.put("Email",newEmail);
                                            object.saveInBackground();
                                        }

                                        new AlertDialog.Builder(UserProfileActivity.this)
                                                .setTitle("User Details Saved Successfully!")
                                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}