package mobile.paluno.de.palaver.gcm;
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;


public class PalaverInstanceIdListenerService extends InstanceIDListenerService {

    public void onTokenRefresh() {
        Intent tokenIntent = new Intent(this,PalaverRegistrationIntentService.class);
        startService(tokenIntent);
    }
}
