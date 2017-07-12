package mobile.paluno.de.palaver.controller;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;
import mobile.paluno.de.palaver.model.ChatAdapter;
import mobile.paluno.de.palaver.model.Message;

public class ChatHistoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String friend;
    private String username;
    private String password;


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

        sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        username = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
        password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);
        friend = (String)getIntent().getSerializableExtra("friend");

        messageList = new ArrayList<Message>();

        refreshList();

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
        mTextView.setText(friend);

        mListView.setAdapter(adapter);

        //get EditText
        mEditText =(EditText)findViewById(R.id.messageEditText);

        //event, when click the send button
        mImageButton = (ImageButton) findViewById(R.id.sendMessageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
    }

    private void refreshList(){
        final JSONObject[] json = new JSONObject[1];

        new Thread((new Runnable() {
            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest();
                try {
                    json[0] = httpRequest.getChatHistory(username, password, friend);
                    if (json[0].getInt("MsgType") == 1) {
                        JSONArray jsonArray = json[0].getJSONArray("Data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            boolean isMine = false;
                            JSONObject tmp = (JSONObject)jsonArray.get(i);
                            if (tmp.getString("Sender").equals(username)) isMine = true;
                            messageList.add(new Message(tmp.getString("Sender"), tmp.getString("Recipient"), tmp.getString("Data"), isMine));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    private void sendMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = mEditText.getText().toString();
                if(!data.equals("")){
                    HttpRequest httpRequest = new HttpRequest();

                    try {
                        JSONObject json = httpRequest.sendMsg(username, password, friend, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);

        username = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
        password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);
    }


    public class LoadChatHistory extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

}
