package mobile.paluno.de.palaver.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private List<Message> messageList;
    private ArrayAdapter<Message> adapter;
    private ChatAdapter chatAdapter;
    private ListView mListView;
    private ImageButton mImageButton;
    private EditText mEditText;
    private TextView mTextView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageList = new ArrayList<Message>();
        messageList.add(new Message(true));
        messageList.add(new Message(true));
        messageList.add(new Message(true));
        messageList.add(new Message(true));
        messageList.add(new Message(false));
        messageList.add(new Message(true));
        messageList.add(new Message(true));
        messageList.add(new Message(false));
        messageList.add(new Message(true));
        messageList.add(new Message(false));
        messageList.add(new Message(true));
        messageList.add(new Message(false));
        messageList.add(new Message(true));

        adapter = new ChatAdapter(ChatHistoryActivity.this, R.layout.chat_in, messageList);

        setContentView(R.layout.activity_palaver_chat);

        //get back button
        backButton =(Button) findViewById(R.id.btn_back_chat);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //get the list view, containing the messages
        mListView = (ListView) findViewById(R.id.msgListView);

        //set the friend name
        mTextView = (TextView) findViewById(R.id.lbFriend);
        String name = (String)getIntent().getSerializableExtra("friend");
        mTextView.setText(name);

        mListView.setAdapter(adapter);

        //get EditText
        mEditText =(EditText)findViewById(R.id.messageEditText);

        //event, when click the send button
        mImageButton= (ImageButton) findViewById(R.id.sendMessageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
