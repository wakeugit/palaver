package mobile.paluno.de.palaver.controller;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mobile.paluno.de.palaver.R;
import mobile.paluno.de.palaver.backend.HttpRequest;
import mobile.paluno.de.palaver.model.Message;

/**
 * Created by wilfried on 21.05.17.
 */

public class ChatsTabFragment extends Fragment {
    private static final String TAG = "ChatsTabFragment";

    private String username;
    private String password;

    private ListView mListView;
    //get kontaktliste mit letzte Nachrichten
    HashMap<String, Message> history = new HashMap<String, Message>();
    private Set<String> friends ;

    private List<String> friend_lastMessage ;

    private ArrayAdapter<String> adapter ;

    public ChatsTabFragment(String username, String password){
        this.username=username;
        this.password=password;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        friend_lastMessage= new ArrayList<>();

        //refreshListView();

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, friend_lastMessage);

        mListView=(ListView) view.findViewById(R.id.listChats) ;
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_[]= (mListView.getItemAtPosition(position)).toString().split("\n");
                String friend_name = friend_[0];
                Intent intent = new Intent(getActivity(), ChatHistoryActivity.class);
                intent.putExtra("friend", friend_name);
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //instanciate LoadContactTask
        new LoadContactTask().execute();


        //new LoadContactTask().execute();
    }

    public void refreshListView(){
        friend_lastMessage.clear();
        String key="";
        Message m=null;
        friends=history.keySet();
        Iterator<String> it = friends.iterator();
        int i=0;
        String du ="";
        while(it.hasNext()){
            key=it.next();
            m=history.get(key);
            if(m.isMine())
                du="Du : ";

            friend_lastMessage.add(key + "\n  "+du+ m.getData());

        }

        /**
         Automatisches aktualisieren der Listview
         Neu eignetippte message sofort in der Liste anzeigen
         */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    public class LoadMessageTask extends AsyncTask<Void, Void, Message> {

        private String friend;

        public LoadMessageTask(String freund){
            friend=freund;
        }


        @Override
        protected Message doInBackground(Void... params) {
            HttpRequest httpRequest = new HttpRequest();
            try {
                JSONObject json = httpRequest.getChatHistory(username, password, friend);
                if (json.getInt("MsgType") == 1) {
                    JSONArray jsonArray = json.getJSONArray("Data");

                    if(jsonArray.length()>=1) {
                        boolean isMine = false;
                        JSONObject tmp = (JSONObject) jsonArray.get(jsonArray.length()-1);
                        if (tmp.getString("Sender").equals(username))
                            isMine = true;
                        return new Message(tmp.getString("Sender"), tmp.getString("Recipient"), tmp.getString("Data"), isMine);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Message message) {
            super.onPostExecute(message);
            if(message!=null){
                //put the last message in the hash map
                history.put(friend, message);

                refreshListView();
            }

        }

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
                        LoadMessageTask messageTask[]=new LoadMessageTask[friend.length()];
                        for (int i=0;i<friend.length();i++){
                            if(friend.get(i)!=null) {
                                messageTask[i] = new LoadMessageTask(friend.get(i).toString());
                                messageTask[i].execute();
                            }

                        }

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
