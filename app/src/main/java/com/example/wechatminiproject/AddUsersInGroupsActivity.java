package com.example.wechatminiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddUsersInGroupsActivity extends AppCompatActivity {

    ListView myListView;
    ArrayAdapter arrayAdapter;
    List<String> users = new ArrayList<>();
    List<String> checkedUsers = new ArrayList<>();
    List<String> usernames = new ArrayList<>();
    List<String> checkedUsernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users_in_groups);

        Objects.requireNonNull(getSupportActionBar()).hide();

        myListView = findViewById(R.id.checkUsersListView);
        myListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView one = view.findViewById(android.R.id.text1);
                one.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                return view;
            }
        };
        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked())
                {
                    checkedUsers.add(users.get(position));
                    checkedUsernames.add(usernames.get(position));
                }
                else
                {
                    checkedUsers.remove(users.get(position));
                    checkedUsernames.remove(usernames.get(position));
                }
            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");

        query.whereNotEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("Division",getIntent().getStringExtra("division"));
        query.whereEqualTo("Year",getIntent().getStringExtra("year"));
        query.whereEqualTo("Branch",getIntent().getStringExtra("branch"));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0 && objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            users.add(object.get("FullName").toString());
                            usernames.add(object.get("Username").toString());
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void NextActivity(View view)
    {
        Intent intent = new Intent(AddUsersInGroupsActivity.this,AddGroupActivity.class);
        String[] selectedUsers = new String[checkedUsers.size()];
        String[] selectedUsersNames = new String[checkedUsernames.size()];

        for(int i=0; i<checkedUsers.size(); i++)
        {
            selectedUsers[i] = checkedUsers.get(i);
            selectedUsersNames[i] = checkedUsernames.get(i);
        }

        intent.putExtra("selectedUsers",selectedUsers);
        intent.putExtra("selectedUserNames", selectedUsersNames);
        startActivity(intent);
    }
}