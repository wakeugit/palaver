package mobile.paluno.de.palaver.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import mobile.paluno.de.palaver.controller.ChatHistoryActivity;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Schwein on 16.07.2017.
 */

public class DefaultNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        String senderName = intent.getStringExtra("sender");
        String preview = intent.getStringExtra("preview");

        Intent notificationIntent = new Intent(context, ChatHistoryActivity.class).putExtra("friend",senderName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Sie haben eine neue Nachricht von " + senderName)
                .setContentText(preview)
                .setVisibility(VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager ) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,mBuilder.build());
    }

}
