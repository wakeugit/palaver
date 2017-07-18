package mobile.paluno.de.palaver.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.IOException;

import mobile.paluno.de.palaver.backend.HttpRequest;


public class PalaverRegistrationIntentService extends IntentService {

    public PalaverRegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    InstanceID instanceID = InstanceID.getInstance(PalaverRegistrationIntentService.this);
                    String token = null;
                    try {
                        token = instanceID.getToken("594324547505", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    HttpRequest request = new HttpRequest();
                    SharedPreferences sharedPreferences = getSharedPreferences("mobile.paluno.de.palaver.login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String password = sharedPreferences.getString("mobile.paluno.de.palaver.Password", null);
                    String username = sharedPreferences.getString("mobile.paluno.de.palaver.Username", null);
                    try {
                        JSONObject response = request.sendToken(username,password,token);
                        System.out.println(response.getString("Info"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

    }
}
