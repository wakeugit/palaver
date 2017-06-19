package mobile.paluno.de.palaver.controller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
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
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest();
                try {
                    JSONObject res = httpRequest.getFreundListe(username, password);
                    if(res.getInt("MsgType")==1){
                        JSONArray friend=res.getJSONArray("Data");
                        friends =new String[friend.length()];
                        for (int i=0;i<friend.length();i++){
                            friends[i]=friend.get(i).toString();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_list_item_1, friends);
            mListView.setAdapter(adapter);

            return view;
       
    }


}
