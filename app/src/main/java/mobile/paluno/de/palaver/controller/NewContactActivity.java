package mobile.paluno.de.palaver.controller;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

public class NewContactActivity extends AppCompatActivity {

    private AddFriendTask mAddFriendTask = null;

    //Speichern und übergeben von Daten an andere Aktivitäten
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String username=null;
    private String password=null;
    private Button backButton;


    // UI references.
    private EditText mKontaktnameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //get back button
        backButton =(Button) findViewById(R.id.btn_back_newcontact);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //get the shared ressource username and password
        sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        username = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
        password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);

        //get the UI reference of the textField name
        mKontaktnameView = (EditText) findViewById(R.id.contact_name);

        Button hinzufuegen = (Button) findViewById(R.id.hinzufuefen);
        hinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset errors.
                mKontaktnameView.setError(null);

                String friend = mKontaktnameView.getText().toString();

                if(TextUtils.isEmpty(friend)){
                    mKontaktnameView.setError(getString(R.string.error_field_required));
                    mKontaktnameView.requestFocus();
                } else if (friend.equals(username)){
                    mKontaktnameView.setError(getString(R.string.error_friend_equals_username));
                    mKontaktnameView.requestFocus();
                    mKontaktnameView.setText(friend + " test");
                }
                else{
                    mAddFriendTask=new AddFriendTask(friend);
                    mAddFriendTask.execute();
                }

            }
        });
    }



    public class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFriend;
        private JSONObject res= new JSONObject();

        AddFriendTask(String kontaktname) {
            mFriend = kontaktname;
        }

        public JSONObject getRes() {
            return res;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Verbindung herstellen mit Palaver Server
            HttpRequest httpRequest = new HttpRequest();

            //Nicht sich selbst adden
            try {
                // Antwort des Palaver Servers
                  res = httpRequest.freundHinzufuegen(username, password, mFriend);
            } catch (Exception e) {
                //Wenn hier nicht klappt JSON Fehler oder kein Internet
                return false;
            }

            int msgType = 0;
            try{
                msgType = res.getInt("MsgType");
            }
            catch (Exception e){
                return false;
            }

            if(msgType == 1) return true;

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddFriendTask = null;

            if (success) {
                //Erfolgreiche Verbidung, navigieren weiter
                try {
                    Toast.makeText(NewContactActivity.this, res.getString("Info"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            } else {

                //Herausstellen von Fehlern
                String info = "";
                try {
                    info = res.getString("Info");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(info.equals("Freund bereits auf der Liste")) {
                    mKontaktnameView.setError(info);
                    mKontaktnameView.requestFocus();
                } else
                    Toast.makeText(NewContactActivity.this, "Fehler", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAddFriendTask = null;
        }
    }

}

