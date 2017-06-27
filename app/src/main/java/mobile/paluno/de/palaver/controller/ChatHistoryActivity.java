package mobile.paluno.de.palaver.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.model.ChatAdapter;
import mobile.paluno.de.palaver.model.Message;

public class ChatHistoryActivity extends AppCompatActivity {

    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<Message> testList = new ArrayList<Message>();
        testList.add(new Message(true));
        testList.add(new Message(true));
        testList.add(new Message(true));
        testList.add(new Message(true));
        testList.add(new Message(false));
        testList.add(new Message(true));
        testList.add(new Message(true));
        testList.add(new Message(false));
        testList.add(new Message(true));
        testList.add(new Message(false));
        testList.add(new Message(true));
        testList.add(new Message(false));
        testList.add(new Message(true));

        chatAdapter = new ChatAdapter(ChatHistoryActivity.this, 0, testList);

        setContentView(R.layout.activity_palaver_chat);

        //get the list view, containing the messages
        ListView mListView = (ListView) findViewById(R.id.msgListView);



        //set the friend name
        TextView friend = (TextView) findViewById(R.id.lbFriend);
        String name = (String)getIntent().getSerializableExtra("friend");
        friend.setText(name);

        //get EditText
        final EditText editText =(EditText)findViewById(R.id.messageEditText);

        //event, when click the send button
        ImageButton send = (ImageButton) findViewById(R.id.sendMessageButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
