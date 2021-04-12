package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupChatsActivity extends AppCompatActivity {

    TextView title;
    ListView groupsListView;
    List<String> GroupNames = new ArrayList<>();
    List<ParseFile> files = new ArrayList<>();
    List<Bitmap> GroupImages = new ArrayList<>();
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        Objects.requireNonNull(getSupportActionBar()).hide();

        title = findViewById(R.id.GroupTitle);

        title.setText(ParseUser.getCurrentUser().getUsername() + "'s Groups");
        groupsListView = findViewById(R.id.groupsListView);


            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Groups");
            query.whereEqualTo("StudentName", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null)
                    {
                        GroupNames.clear();
                        files.clear();
                        GroupImages.clear();

                        if(objects != null && objects.size() > 0)
                        {
                            System.out.println("Inside double if");
                            for(ParseObject object : objects)
                            {
                                GroupNames.add(object.get("GroupName").toString());
                                ParseFile groupIcon = (ParseFile) object.get("GroupIcon");
                                files.add(groupIcon);
                            }

                            myAdapter = new MyAdapter(GroupChatsActivity.this,GroupNames,GroupImages);
                            groupsListView.setAdapter(myAdapter);
                        }
                    }
                }
            });

            if(GroupNames.size() == 0)
            {
                System.out.println("inside zero if");
                GroupNames.add("Opps Looks Like you Don't Have Any Groups.\nCreate A New Group!");
                myAdapter = new MyAdapter(GroupChatsActivity.this,GroupNames,GroupImages);
                groupsListView.setAdapter(myAdapter);
            }

    }

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        List<String> GroupNames;
        List<Bitmap> GroupImages;

        MyAdapter(Context context, List<String> GroupNames, List<Bitmap> GroupImages)
        {
            super(context,R.layout.row,R.id.fullNameUser, GroupNames);
            this.context = context;
            this.GroupNames = GroupNames;
            this.GroupImages = GroupImages;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView profileImage = row.findViewById(R.id.image);
            TextView name = row.findViewById(R.id.fullNameUser);

            if(files.size() > 0)
            {
                files.get(position).getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if(e == null && data != null)
                        {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                            profileImage.setImageBitmap(bitmap);
                            name.setText(GroupNames.get(position));
                        }
                    }
                });
            }
            else
            {
                name.setText(GroupNames.get(position));
                name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                profileImage.setVisibility(View.INVISIBLE);
            }

            return row;
        }
    }
}