package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FilesDMActivity extends AppCompatActivity {

    String username = "";
    String fullName = "";
    String messageToReciever = "";
    ListView mySendFilesListView;

    List<Map<String, String>> fileData;
    SimpleAdapter simpleAdapter;
    List<String> senders = new ArrayList<>();
    List<String> reciever = new ArrayList<>();
    List<String> fileMessages = new ArrayList<>();
    List<String> filesURLS = new ArrayList<>();
    List<String> fileNames = new ArrayList<>();
    List<byte[]> bytesArrays = new ArrayList<>();
//    List<Bitmap> myBitMapArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_d_m);

        Objects.requireNonNull(getSupportActionBar()).hide();

        fullName = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");

        mySendFilesListView = findViewById(R.id.mySendFilesListView);

        TextView title = findViewById(R.id.sendDMFile);
        title.setText(getIntent().getStringExtra("name"));

        ParseQuery<ParseObject> queryOne = new ParseQuery<ParseObject>("DMSFiles");
        queryOne.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        queryOne.whereEqualTo("recipient",username);

        ParseQuery<ParseObject> queryTwo = new ParseQuery<ParseObject>("DMSFiles");
        queryTwo.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());
        queryTwo.whereEqualTo("sender",username);

        ArrayList<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(queryOne);
        queries.add(queryTwo);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    senders.clear();
                    reciever.clear();
                    fileMessages.clear();
                    bytesArrays.clear();
                    if(objects.size() > 0 && objects != null)
                    {
                        fileData = new ArrayList<>();
                        for(ParseObject object : objects)
                        {
                            Map<String, String> chatInfo = new HashMap<>();
                            chatInfo.put("Message",object.get("FileMessage").toString());
                            chatInfo.put("Name",object.get("sender").toString());

                            senders.add(object.get("sender").toString());
                            reciever.add(object.get("recipient").toString());
                            fileMessages.add(object.get("FileMessage").toString());

                            ParseFile file = (ParseFile) object.get("File");
                            filesURLS.add(file.getUrl());
                            fileNames.add(file.getName());
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null && data != null)
                                    {
                                        bytesArrays.add(data);
                                    }
                                }
                            });
                            fileData.add(chatInfo);
                        }
                        simpleAdapter = new SimpleAdapter(FilesDMActivity.this, fileData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

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
                        mySendFilesListView.setAdapter(simpleAdapter);
                    }
                }
            }
        });

        mySendFilesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FilesDMActivity.this);
                builder.setTitle("Do you want to Download the file?");


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(filesURLS.get(position));

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(fileNames.get(position));
                        request.setDescription("Downloading");
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileNames.get(position));
                        downloadmanager.enqueue(request);

                        Toast.makeText(FilesDMActivity.this, "File has been Successfully Downloaded in your File Manager.",Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });

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

    public void SelectFilesFromManager(View view)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getDocument();
                }
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
                byte[] inputData = getBytes(inputStream);
                bytesArrays.add(inputData);

                ParseObject object = new ParseObject("DMSFiles");
                object.put("sender", ParseUser.getCurrentUser().getUsername());
                object.put("recipient",username);
                object.put("FileMessage",messageToReciever);

                ParseFile file = null;

                if(GetFileExtension(selectedFile).contains("pdf"))
                {
                    file = new ParseFile("file.pdf",inputData);
                }
                else if(GetFileExtension(selectedFile).contains("ppt"))
                {
                    file = new ParseFile("file.ppt",inputData);
                }
                else if(GetFileExtension(selectedFile).contains("doc"))
                {
                    file = new ParseFile("file.doc",inputData);
                }

                filesURLS.add(file.getUrl());
                fileNames.add(file.getName());

                object.put("File",file);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                        {
                            Map<String, String> chatInfo = new HashMap<>();

                            chatInfo.put("Message",messageToReciever);
                            chatInfo.put("Name",ParseUser.getCurrentUser().getUsername());

                            senders.add(ParseUser.getCurrentUser().getUsername());
                            reciever.add(username);
                            fileMessages.add(messageToReciever);

                            if(fileData == null)
                            {
                                fileData = new ArrayList<>();
                                System.out.println("NULL HAI");
                            }
                            else
                            {
                                System.out.println("NOT NULL");
                            }
                            fileData.add(chatInfo);
                            simpleAdapter = new SimpleAdapter(FilesDMActivity.this, fileData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

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
                            mySendFilesListView.setAdapter(simpleAdapter);
                        }
                    }
                });

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

    public class DownloadFileFromUrl extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... urls) {
            int count;
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString() + "/2011.pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
    }

}