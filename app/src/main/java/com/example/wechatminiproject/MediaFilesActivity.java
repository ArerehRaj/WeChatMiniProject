package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MediaFilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_files);

        getSupportActionBar().hide();
    }

    public void sendToDmList(View view)
    {
        Intent intent = new Intent(MediaFilesActivity.this, ChatsActivity.class);
        intent.putExtra("division",getIntent().getStringExtra("division"));
        intent.putExtra("year",getIntent().getStringExtra("year"));
        intent.putExtra("branch",getIntent().getStringExtra("branch"));
        intent.putExtra("Code",3);
        startActivity(intent);
    }

    public void sendToGroupsList(View view)
    {
        Intent intent = new Intent(MediaFilesActivity.this, GroupChatsActivity.class);
        intent.putExtra("division",getIntent().getStringExtra("division"));
        intent.putExtra("year",getIntent().getStringExtra("year"));
        intent.putExtra("branch",getIntent().getStringExtra("branch"));
        intent.putExtra("CODE",30);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void selectFiles(View view)
    {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},30);
        }
        else
        {
            getDocument();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getDocument()
    {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, 30);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SelectImage(View view)
    {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},30);
        }
        else
        {
            getDocument();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 30)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getDocument();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedFile = null;
        if(data != null)
        {
            selectedFile = data.getData();
        }
        if(requestCode == 30 && resultCode == RESULT_OK && data != null)
        {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFile);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                System.out.println("URI " + selectedFile);
                System.out.println("File path " + GetFileExtension(selectedFile));
                byte[] inputData = getBytes(inputStream);
                System.out.println("Bytes " + Arrays.toString(inputData));
                ParseObject object = new ParseObject("DMSImages");
                object.put("sender","arerehraj");
                object.put("recipient","ebu");
                ParseFile file = new ParseFile("newFile.docx",inputData);
                object.put("Images",file);

                object.saveInBackground();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}