package mobile.paluno.de.palaver.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Schwein on 12.07.2017.
 */

public class PalaverGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        Log.d("OnMessageRecivedMethode","");
    }
}
