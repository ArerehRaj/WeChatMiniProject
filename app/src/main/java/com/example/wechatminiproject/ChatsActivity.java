package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class ChatsActivity extends AppCompatActivity {

    List<String> fullNames = new ArrayList<>();
    List<Bitmap> profileImages = new ArrayList<>();
    List<ParseFile> files = new ArrayList<>();

    ListView chatsListView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        getSupportActionBar().hide();

        String branch = getIntent().getStringExtra("branch");
        String year = getIntent().getStringExtra("year");
        String division = getIntent().getStringExtra("division");

        chatsListView = findViewById(R.id.myChatsListView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");

        query.whereNotEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("Division",division);
        query.whereEqualTo("Branch",branch);
        query.whereEqualTo("Year",year);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    fullNames.clear();
                    profileImages.clear();
                    files.clear();

                    if(objects.size() > 0 && objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            fullNames.add(object.get("FullName").toString());
                            ParseFile image = (ParseFile) object.get("ProfileImage");
                            files.add(image);
                        }

                        System.out.println("Size of images Array " + profileImages.size());

                        myAdapter = new MyAdapter(ChatsActivity.this,fullNames,profileImages);
                        chatsListView.setAdapter(myAdapter);
                        chatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(ChatsActivity.this,fullNames.get(position) + " Clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        List<String> names;
        List<Bitmap> userImages;

        MyAdapter(Context context, List<String> names, List<Bitmap> userImages)
        {
            super(context,R.layout.row,R.id.fullNameUser, names);
            this.context = context;
            this.names = names;
            this.userImages = userImages;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView profileImage = row.findViewById(R.id.image);
            TextView name = row.findViewById(R.id.fullNameUser);

            files.get(position).getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e == null && data != null)
                    {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                        profileImage.setImageBitmap(bitmap);
                        name.setText(fullNames.get(position));
                    }
                }
            });

            return row;
        }
    }


}