package mobile.paluno.de.palaver.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import mobile.paluno.de.palaver.controller.ChatHistoryActivity;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;


public class PalaverGcmListenerService extends GcmListenerService {

    public static boolean isLoggedIn =true;

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        if(!isLoggedIn)
            return;

        String senderName = bundle.getString("sender");
        String preview = bundle.getString("preview");


        Intent intent = new Intent();
        intent.setAction("mobile.paluno.de.palaver.notification");
        intent.putExtra("sender", senderName);
        intent.putExtra("preview", preview);
        getApplication().sendOrderedBroadcast(intent,null);

    }
}
