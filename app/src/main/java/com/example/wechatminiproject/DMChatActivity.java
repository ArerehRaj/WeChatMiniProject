package com.example.wechatminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DMChatActivity extends AppCompatActivity {

    TextView title;
    ListView chatsListView;
    ArrayAdapter<String> arrayAdapter;
    List<String> messageList = new ArrayList<>();
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_m_chat);

        getSupportActionBar().hide();

        title = findViewById(R.id.ChatWithTitle);
        title.setText(getIntent().getStringExtra("name"));

        username = getIntent().getStringExtra("username");

        chatsListView = findViewById(R.id.dmsListView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, messageList);
        chatsListView.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> queryOne = new ParseQuery<ParseObject>("DMS");
        queryOne.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        queryOne.whereEqualTo("recipient",username);

        ParseQuery<ParseObject> queryTwo = new ParseQuery<ParseObject>("DMS");
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
                    if(objects != null && objects.size() > 0)
                    {
                        messageList.clear();
                        for(ParseObject object : objects)
                        {
                            String messageContent = object.get("message").toString();
                            if(object.get("sender") != ParseUser.getCurrentUser().getUsername())
                            {
                                messageContent = "> " + "messageContent";
                            }
                            messageList.add(messageContent);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    public void sendMessage(View view)
    {
        EditText editTextMessage = findViewById(R.id.editTextMesage);
        String messageContent = editTextMessage.getText().toString();

        ParseObject message = new ParseObject("DMS");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient",username);
        message.put("message",messageContent);

        editTextMessage.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    messageList.add(messageContent);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}