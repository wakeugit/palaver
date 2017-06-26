package mobile.paluno.de.palaver.controller;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import mobile.paluno.de.palaver.R;

/**
 * Created by wilfried on 21.05.17.
 */

public class ChatsTabFragment extends Fragment {
    private static final String TAG = "ChatsTabFragment";

    private ListView mListView;
    private String[] last_chats = new String[]{
            "Antoine\n Hi!", "Benoit\n Hello..", "Cyril\n Bye", "David\n Bist du da?", "Eloise \n Wo bist du denn?" };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        mListView=(ListView) view.findViewById(R.id.listChats) ;

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, last_chats);
        mListView.setAdapter(adapter);

        return view;
    }
}
