package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AddInfoActivity extends AppCompatActivity {

    EditText editTextUserNameProfile;
    EditText editTextUserEmailProfile;
    EditText editTextUserFullNameProfile;
    EditText editTextUserBranchProfile;
    EditText editTextUserYearProfile;
    EditText editTextUserDivisionProfile;
    EditText editTextUserGenderProfile;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        Objects.requireNonNull(getSupportActionBar()).hide();

        editTextUserNameProfile = findViewById(R.id.editTextUserNameProfile);
        editTextUserFullNameProfile = findViewById(R.id.editTextFullName);
        editTextUserEmailProfile = findViewById(R.id.editTextUserEmailProfile);
        editTextUserBranchProfile = findViewById(R.id.editTextUserBranch);
        editTextUserYearProfile = findViewById(R.id.editTextUserYear);
        editTextUserDivisionProfile = findViewById(R.id.editTextUserDiv);
        editTextUserGenderProfile = findViewById(R.id.editTextUserGender);

        editTextUserNameProfile.setText(getIntent().getStringExtra("username"));
        editTextUserEmailProfile.setText(getIntent().getStringExtra("userEmail"));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SelectImage(View view)
    {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else
        {
            getPhoto();
        }
    }

    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = null;
        if(data != null)
        {
            selectedImage = data.getData();
        }
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ImageView profileImage = findViewById(R.id.profileImage);
                profileImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void SaveDetails(View view)
    {
        String email = editTextUserEmailProfile.getText().toString();
        String username = editTextUserNameProfile.getText().toString();
        String fullName = editTextUserFullNameProfile.getText().toString();
        String branch = editTextUserBranchProfile.getText().toString();
        String year = editTextUserYearProfile.getText().toString();
        String division = editTextUserDivisionProfile.getText().toString();
        String gender = editTextUserGenderProfile.getText().toString();

        if(username.isEmpty())
        {
            editTextUserNameProfile.setError("Please Enter Your Username.");
            return;
        }

        if(email.isEmpty())
        {
            editTextUserEmailProfile.setError("Please Enter Your Email.");
            return;
        }

        if(fullName.isEmpty())
        {
            editTextUserFullNameProfile.setError("Please Enter Your Full Name.");
            return;
        }

        if(branch.isEmpty())
        {
            editTextUserBranchProfile.setError("Please Enter Your Branch.");
            return;
        }

        if(year.isEmpty())
        {
            editTextUserYearProfile.setError("Please Enter Your Year Of Study.");
            return;
        }

        if(division.isEmpty())
        {
            editTextUserDivisionProfile.setError("Please Enter Your Division.");
            return;
        }

        if(gender.isEmpty())
        {
            editTextUserGenderProfile.setError("Please Enter Your Gender.");
            return;
        }

        new AlertDialog.Builder(AddInfoActivity.this)
                .setTitle("Save Details")
                .setMessage("Please verify your Details Properly.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                        byte[] byteArray = stream.toByteArray();

                        ParseFile profileImage = new ParseFile("image.png", byteArray);

                        ParseObject student = new ParseObject("Students");

                        student.put("ProfileImage", profileImage);
                        student.put("Username", username);
                        student.put("FullName", fullName);
                        student.put("Email", email);
                        student.put("Branch",branch);
                        student.put("Year",year);
                        student.put("Division",division);
                        student.put("Gender",gender);

                        student.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                String message = "";
                                if(e == null)
                                {
                                    message = "User Details Saved";
                                }
                                else
                                {
                                    message = "An Error Occurred";
                                }

                                Toast.makeText(AddInfoActivity.this,message,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("No",null)
                .show();

    }
}