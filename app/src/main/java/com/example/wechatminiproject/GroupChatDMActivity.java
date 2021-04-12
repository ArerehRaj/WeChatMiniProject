package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupChatDMActivity extends AppCompatActivity {

    String groupName = "";
    ListView groupsListView;
    SimpleAdapter simpleAdapter;
    EditText editTextGroupMessages;
    List<Map<String, String>> groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_d_m);

        Objects.requireNonNull(getSupportActionBar()).hide();

        groupName = getIntent().getStringExtra("name");

        TextView myTitle = findViewById(R.id.textViewGroupTitle);
        myTitle.setText(groupName);

        groupsListView = findViewById(R.id.groupChatsDMMessages);

        editTextGroupMessages = findViewById(R.id.editTextGroupMessages);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(groupName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects != null && objects.size() > 0)
                    {
                        groupData = new ArrayList<>();
                        for(ParseObject object : objects)
                        {
                            Map<String, String> chatInfo = new HashMap<>();
                            chatInfo.put("Message",object.get("Messages").toString());
                            chatInfo.put("Name",object.get("Username").toString());
                            groupData.add(chatInfo);
                        }
                        simpleAdapter = new SimpleAdapter(GroupChatDMActivity.this, groupData, android.R.layout.simple_list_item_2, new String[] {"Message","Name"},new int[] {android.R.id.text1, android.R.id.text2}){

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
                        groupsListView.setAdapter(simpleAdapter);
                    }
                }
            }
        });
    }

    public void sendMessageToGroup(View view)
    {
        String messageContent = editTextGroupMessages.getText().toString();
        ParseObject message = new ParseObject(groupName);
        message.put("Username",ParseUser.getCurrentUser().getUsername());
        message.put("Messages",messageContent);

        editTextGroupMessages.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Map<String, String> chatInfo = new HashMap<>();
                    chatInfo.put("Message",messageContent);
                    chatInfo.put("Name",ParseUser.getCurrentUser().getUsername());
                    groupData.add(chatInfo);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}