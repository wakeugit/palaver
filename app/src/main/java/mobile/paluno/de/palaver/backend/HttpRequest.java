package mobile.paluno.de.palaver.backend;

/**
 * Created by wilfried on 13.05.17.
 */

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.paluno.de.palaver.controller.PalaverLoginActivity;

public class HttpRequest {

        public JSONObject benutzerRegistrieren(String username, String password) throws Exception{

            JSONObject response = new JSONObject();

            URL url=new URL("http://palaver.se.paluno.uni-due.de/api/user/register");
            HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestMethod("POST");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //String urlParameters = "{\"Username\":\"wilfried\",\"Password\":\"wilfried\"}"; // It's your JSON-array
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            OutputStream os = httpcon.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            InputStream is = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // get output from the server
            String line = null;
            while((line = br.readLine() ) != null) {

                try {
                    response=new JSONObject(line);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
            httpcon.disconnect();

            return response;
        }

    public JSONObject benutzerpasswortValidate(String username, String password) throws Exception{

        JSONObject response = new JSONObject();

        URL url=new URL("http://palaver.se.paluno.uni-due.de/api/user/validate");
        HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
        httpcon.setDoOutput(true);
        httpcon.setRequestMethod("POST");
        httpcon.setRequestProperty("Accept", "application/json");
        httpcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        JSONObject json = new JSONObject();

        try {
            json.put("Username", username);
            json.put("Password", password);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        OutputStream os = httpcon.getOutputStream();
        os.write(json.toString().getBytes("UTF-8"));
        os.close();

        InputStream is = httpcon.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // get output from the server
        String line = null;
        while((line = br.readLine() ) != null) {

            try {
                response=new JSONObject(line);
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        httpcon.disconnect();

        return response;
    }




}
