package mobile.paluno.de.palaver.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Schwein on 12.07.2017.
 */

public class PalaverRegistrationIntentService extends IntentService {

    public PalaverRegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("594324547505", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
           // ((Palaver)getApplication()).sendTokenToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
