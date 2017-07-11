package mobile.paluno.de.palaver.controller;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

/**
 * Created by wilfried on 21.05.17.
 */

public class ContactsTabFragment extends Fragment {
    private static final String TAG = "ChatsTabFragment";

    private String username;
    private String password;

    private ListView mListView;

    private String[] friends;

    public ContactsTabFragment(String username, String password){
        this.username=username;
        this.password=password;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mListView=(ListView) view.findViewById(R.id.listContacts) ;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_name = (mListView.getItemAtPosition(position)).toString();
                Intent intent = new Intent(getActivity(), ChatHistoryActivity.class);
                intent.putExtra("friend", friend_name);
                startActivity(intent);
            }
        });

        //instanciate LoadContactTask
        //new LoadContactTask().execute();

        return view;
       
    }

    @Override
    public void onResume() {
        super.onResume();
        //instanciate LoadContactTask
        new LoadContactTask().execute();
    }

    public class LoadContactTask extends AsyncTask<Void, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpRequest httpRequest = new HttpRequest();
            JSONObject res=null;
            try {
                res = httpRequest.getFreundListe(username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(JSONObject res) {
            super.onPostExecute(res);
            if(res!=null){
                try {
                    if(res.getInt("MsgType") == 1){
                        JSONArray friend = res.getJSONArray("Data");
                        friends = new String[friend.length()];
                        for (int i=0;i<friend.length();i++){
                            friends[i]=friend.get(i).toString();
                        }
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, friends);/*{
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                // Get the current item from ListView
                                View view = super.getView(position,convertView,parent);

                                // Get the Layout Parameters for ListView Current Item View
                                ViewGroup.LayoutParams params = view.getLayoutParams();

                                // Set the height of the Item View

                                params.height = 150;

                                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                                // Set the text size 25 dip for ListView each item
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,23);


                                view.setLayoutParams(params);

                                return view;
                            }

                        };*/
                        mListView.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                //
                Toast.makeText(getActivity(), "Fehler", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
