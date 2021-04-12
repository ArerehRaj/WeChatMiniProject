package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AddGroupActivity extends AppCompatActivity {

    EditText editTextGroupName;
    String[] list;
    String[] listUsernames;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent myIntent = getIntent();

        list =  myIntent.getStringArrayExtra("selectedUsers");
        listUsernames = myIntent.getStringArrayExtra("selectedUserNames");//.getStringArrayExtra("selectedUsers");

        editTextGroupName = findViewById(R.id.editTextGroupName);

    }

    public void CreateGroup(View view)
    {
        String groupName = editTextGroupName.getText().toString();

        if(groupName.isEmpty())
        {
            editTextGroupName.setError("Please Enter A Group Name!");
            return;
        }

        for(int i=0; i<listUsernames.length; i++)
        {
            ParseObject group = new ParseObject("Groups");
            group.put("GroupName",groupName);
            group.put("StudentName",listUsernames[i]);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();

            ParseFile groupImage = new ParseFile("image.png", byteArray);

            group.put("GroupIcon",groupImage);

            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        Toast.makeText(AddGroupActivity.this,"SUCCESS",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AddGroupActivity.this,"UNSUCCESSFUL",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ParseObject group = new ParseObject("Groups");
        group.put("GroupName",groupName);
        group.put("StudentName", ParseUser.getCurrentUser().getUsername());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();

        ParseFile groupImage = new ParseFile("image.png", byteArray);

        group.put("GroupIcon",groupImage);

        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
//                    Toast.makeText(AddGroupActivity.this,"SUCCESS",Toast.LENGTH_SHORT).show();
                    for(int i=0; i<listUsernames.length; i++)
                    {
                        ParseObject groupMembers = new ParseObject(groupName);
                        groupMembers.put("Username",listUsernames[i]);
                        groupMembers.put("Messages","Hi");
                        groupMembers.saveInBackground();
                    }

                    ParseObject groupMembers = new ParseObject(groupName);
                    groupMembers.put("Username",ParseUser.getCurrentUser().getUsername());
                    groupMembers.put("Messages","Hi from " + ParseUser.getCurrentUser().getUsername());
                    groupMembers.saveInBackground();

                    Intent intent = new Intent(AddGroupActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(AddGroupActivity.this,"UNSUCCESSFUL",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selectImageGroup(View view)
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
                ImageView groupImage = findViewById(R.id.imageViewGroupIcon);
                groupImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}