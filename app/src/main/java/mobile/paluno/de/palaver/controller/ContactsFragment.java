package mobile.paluno.de.palaver.controller;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;

/**
 * Created by wilfried on 21.05.17.
 */

public class ContactsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    private String username=null;
    private String password=null;

    private ListView mListView;
    private String[] friends;

    public ContactsFragment(String username, String password){
        this.username=username;
        this.password=password;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mListView=(ListView) view.findViewById(R.id.listContacts) ;

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
                    if(res.getInt("MsgType")==1){
                        JSONArray friend=res.getJSONArray("Data");
                        friends =new String[friend.length()];
                        for (int i=0;i<friend.length();i++){
                            friends[i]=friend.get(i).toString();
                        }
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, friends);
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
