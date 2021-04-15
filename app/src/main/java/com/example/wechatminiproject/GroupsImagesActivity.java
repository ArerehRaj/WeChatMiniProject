package com.example.wechatminiproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupsImagesActivity extends AppCompatActivity {

    TextView title;
    ListView myGroupsImagesListView;
    SimpleAdapter simpleAdapter;
    List<Map<String, String>> groupImagesData;
    String groupName = "";
    List<byte[]> bytesArrays = new ArrayList<>();
    List<String> imageMessages = new ArrayList<>();
    String messageToReciever = "";
    List<Bitmap> myBitMapArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_images);

        Objects.requireNonNull(getSupportActionBar()).hide();

        title = findViewById(R.id.groupTitleImages);
        myGroupsImagesListView = findViewById(R.id.myGroupsImages);

        groupName = getIntent().getStringExtra("name");

        title.setText(groupName);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(groupName.replace(" ",""));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects != null && objects.size() > 0)
                    {
                        groupImagesData = new ArrayList<>();
                        for(ParseObject object : objects)
                        {
                            if(object.get("Username") != null && object.get("ImageMessages") != null && object.get("Images") != null)
                            {
                                Map<String, String> imageChatInfo = new HashMap<>();

                                imageChatInfo.put("Message",object.get("ImageMessages").toString());
                                imageChatInfo.put("Name",object.get("Username").toString());
                                imageMessages.add(object.get("ImageMessages").toString());

                                ParseFile imageGroupDM = (ParseFile) object.get("Images");
                                imageGroupDM.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if(e == null && data != null)
                                        {
                                            bytesArrays.add(data);
                                        }
                                    }
                                });
                                groupImagesData.add(imageChatInfo);
                            }
//                            groupImagesData.add(ima)
                        }
                        simpleAdapter = new SimpleAdapter(GroupsImagesActivity.this, groupImagesData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {

                                View view = super.getView(position, convertView, parent);

                                TextView one = view.findViewById(android.R.id.text1);
                                TextView two = view.findViewById(android.R.id.text2);

                                one.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                                two.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                if(two.getText().equals(ParseUser.getCurrentUser().getUsername()))
                                {
                                    one.setGravity(Gravity.END);
                                    two.setGravity(Gravity.END);
                                }

                                return view;
                            }
                        };
                        myGroupsImagesListView.setAdapter(simpleAdapter);
                    }
                }
            }
        });

        myGroupsImagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupsImagesActivity.this,ViewDMImageActivity.class);
                intent.putExtra("message", imageMessages.get(position));
                intent.putExtra("data",bytesArrays.get(position));
                startActivity(intent);
            }
        });
    }

    public void selectImage(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Write the message for the image");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageToReciever = input.getText().toString();
                getPhoto();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            ClipData clipData = data.getClipData();
            if(clipData != null)
            {
                for(int i=0; i< clipData.getItemCount(); i++)
                {
                    Uri imageURI = clipData.getItemAt(i).getUri();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageURI);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        myBitMapArrayList.add(bitmap);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                Uri imageURI = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageURI);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    myBitMapArrayList.add(bitmap);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            for(int i=0; i<myBitMapArrayList.size(); i++)
            {
                ParseObject newImage = new ParseObject(groupName.replace(" ",""));

                newImage.put("Username",ParseUser.getCurrentUser().getUsername());
                newImage.put("ImageMessages",messageToReciever);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitMapArrayList.get(i).compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();

                bytesArrays.add(byteArray);

                ParseFile image = new ParseFile("image.png", byteArray);
                newImage.put("Images",image);

                newImage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                        {
                            Map<String, String> imageChatInfo = new HashMap<>();

                            imageChatInfo.put("Message",messageToReciever);
                            imageChatInfo.put("Name",ParseUser.getCurrentUser().getUsername());

                            imageMessages.add(messageToReciever);

                            if(groupImagesData == null)
                            {
                                groupImagesData = new ArrayList<>();
                                System.out.println("NULL HAI");
                            }
                            else
                            {
                                System.out.println("NOT NULL");
                            }

                            groupImagesData.add(imageChatInfo);
//                            simpleAdapter.notifyDataSetChanged();
                            simpleAdapter = new SimpleAdapter(GroupsImagesActivity.this, groupImagesData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {

                                    View view = super.getView(position, convertView, parent);

                                    TextView one = view.findViewById(android.R.id.text1);
                                    TextView two = view.findViewById(android.R.id.text2);

                                    one.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                                    two.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                    if(two.getText().equals(ParseUser.getCurrentUser().getUsername()))
                                    {
                                        one.setGravity(Gravity.END);
                                        two.setGravity(Gravity.END);
                                    }

                                    return view;
                                }
                            };
                            myGroupsImagesListView.setAdapter(simpleAdapter);
                        }
                    }
                });
            }

        }
    }

}