package mobile.paluno.de.palaver.model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobile.paluno.de.palaver.R;

/**
 * Created by wilfried on 27.06.17.
 */

public class ChatAdapter extends ArrayAdapter<Message> {

    private Activity activity;
    private List<Message> messages;

    public ChatAdapter(Activity context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println(position);
        System.out.println(convertView);
        System.out.println(parent);

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        Message chatMessage = messages.get(position);
        int viewType = getItemViewType(position);

        if (chatMessage.isMine()) {
            layoutResource = R.layout.chat_out;
        } else {
            layoutResource = R.layout.chat_in;
        }

        //Behebt das Problem mit den falsch positionierten Bubbles

//        if (convertView != null) {
//            holder = (ViewHolder) convertView.getTag();
//        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
//        }

        //set message content
        holder.msg.setText(chatMessage.getData());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
        }
    }
}
