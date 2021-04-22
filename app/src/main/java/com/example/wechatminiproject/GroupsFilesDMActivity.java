package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupsFilesDMActivity extends AppCompatActivity {

    TextView title;
    String groupName = "";
    ListView GroupFilesDMListView;
    SimpleAdapter simpleAdapter;
    List<Map<String, String>> groupFilesData;
    List<String> fileMessages = new ArrayList<>();
    List<String> fileUrls = new ArrayList<>();
    List<String> fileNames = new ArrayList<>();
    List<byte[]> bytesArray = new ArrayList<>();
    String messageToReciever = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_files_d_m);

        Objects.requireNonNull(getSupportActionBar()).hide();
        title = findViewById(R.id.groupTitleFiles);

        groupName = getIntent().getStringExtra("name");
        title.setText(groupName);

        GroupFilesDMListView = findViewById(R.id.GroupFilesDMListView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(groupName.replace(" ",""));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects != null && objects.size() > 0)
                    {
                        fileMessages.clear();
                        fileNames.clear();
                        fileUrls.clear();
                        groupFilesData = new ArrayList<>();

                        for(ParseObject object : objects)
                        {
                            if(object.get("Username") != null && object.get("FileMessage") != null && object.get("File") != null)
                            {
                                Map<String, String> fileChatInfo = new HashMap<>();
                                fileChatInfo.put("Message",object.get("FileMessage").toString());
                                fileChatInfo.put("Name",object.get("Username").toString());
                                fileMessages.add(object.get("FileMessage").toString());

                                ParseFile file = (ParseFile) object.get("File");
                                fileNames.add(file.getName());
                                fileUrls.add(file.getUrl());

                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if(e == null && data != null)
                                        {
                                            bytesArray.add(data);
                                        }
                                    }
                                });
                                groupFilesData.add(fileChatInfo);
                            }
                        }

                        simpleAdapter = new SimpleAdapter(GroupsFilesDMActivity.this, groupFilesData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

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
                        GroupFilesDMListView.setAdapter(simpleAdapter);
                    }
                }
            }
        });

        GroupFilesDMListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsFilesDMActivity.this);
                builder.setTitle("Do you want to Download the file?");


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(fileUrls.get(position));

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(fileNames.get(position));
                        request.setDescription("Downloading");
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileNames.get(position));
                        downloadmanager.enqueue(request);

                        Toast.makeText(GroupsFilesDMActivity.this, "File has been Successfully Downloaded in your File Manager.",Toast.LENGTH_LONG).show();
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
    public void getFile()
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
        builder.setTitle("Write the message for the file");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageToReciever = input.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getFile();
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
                getFile();
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
                bytesArray.add(inputData);

                ParseObject object = new ParseObject(groupName.replace(" ",""));

                object.put("Username", ParseUser.getCurrentUser().getUsername());
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

                fileUrls.add(file.getUrl());
                fileNames.add(file.getName());

                object.put("File",file);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                        {
                            Map<String, String> fileChatInfo = new HashMap<>();
                            fileChatInfo.put("Message",object.get("FileMessage").toString());
                            fileChatInfo.put("Name",object.get("Username").toString());
                            fileMessages.add(object.get("FileMessage").toString());

                            if(groupFilesData == null)
                            {
                                groupFilesData = new ArrayList<>();
                                System.out.println("NULL HAI");
                            }
                            else
                            {
                                System.out.println("NOT NULL");
                            }

                            groupFilesData.add(fileChatInfo);

                            simpleAdapter = new SimpleAdapter(GroupsFilesDMActivity.this, groupFilesData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

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

                            GroupFilesDMListView.setAdapter(simpleAdapter);
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