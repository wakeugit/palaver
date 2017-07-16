package mobile.paluno.de.palaver.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mobile.paluno.de.palaver.controller.ChatHistoryActivity;

public class InChatNotificationReceiver extends BroadcastReceiver {

    private NotificationListener notificationListener;

    public InChatNotificationReceiver(NotificationListener listener) {
        notificationListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String senderName = intent.getStringExtra("sender");
        if(senderName.equals(notificationListener.getChatName())) {
            notificationListener.onNotificationReceived();
            abortBroadcast();
        }
    }
}
