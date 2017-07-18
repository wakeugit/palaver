package mobile.paluno.de.palaver.gcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class PalaverGcmListenerService extends GcmListenerService {

    public static boolean isLoggedIn = true;

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
        getApplication().sendOrderedBroadcast(intent, null);
    }
}
