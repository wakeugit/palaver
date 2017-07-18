package mobile.paluno.de.palaver.backend;

/**
 * Created by wilfried on 13.05.17.
 */

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class HttpRequest {

    private JSONObject verbindungHerstellen(String _url, JSONObject json) throws Exception{
        //HTTP Verbindung herstellen und an Server "POST"en
        URL url=new URL(_url);
        HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
        httpcon.setDoOutput(true);
        httpcon.setRequestMethod("POST");
        //httpcon.setRequestProperty("Accept", "application/json");
        httpcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        //Request als Outputstream des JSON Strings in Bytes
        OutputStream os = httpcon.getOutputStream();
        os.write(json.toString().getBytes("UTF-8"));
        os.close();

        //Antwort  als Inputstream und umwandeln in JSON String
        InputStream is = httpcon.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        JSONObject response = new JSONObject();
        String line;
        while((line = br.readLine() ) != null) {
            try {
                response = new JSONObject(line);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        httpcon.disconnect();

        return response;
    }

        public JSONObject benutzerRegistrieren(String username, String password) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/user/register",
                    json);
        }

        public JSONObject benutzerValidate(String username, String password) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/user/validate",
                    json);
         }

//        public JSONObject freundVerwalten(String _url, String username, String password, String friend) throws Exception{
//
//            JSONObject json = new JSONObject();
//
//            try {
//                json.put("Username", username);
//                json.put("Password", password);
//                if (friend!=null) {
//                    json.put("Friend", friend);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            //HTTP Verbindung herstellen und an Server POSTen
//            URL url=new URL(_url);
//            HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
//            httpcon.setDoOutput(true);
//            httpcon.setRequestMethod("POST");
//            //httpcon.setRequestProperty("Accept", "application/json");
//            httpcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//
//            //Request als Outputstream des JSON Strings in Bytes
//            OutputStream os = httpcon.getOutputStream();
//            os.write(json.toString().getBytes("UTF-8"));
//            os.close();
//
//            //Antwort  als Inputstream und umwandeln in JSON String
//            InputStream is = httpcon.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            JSONObject response = new JSONObject();
//            String line;
//            while((line = br.readLine() ) != null) {
//                try {
//                    response = new JSONObject(line);
//                } catch (JSONException e){
//                    e.printStackTrace();
//                }
//
//            }
//
//            httpcon.disconnect();
//
//            return response;
//        }

        public JSONObject freundHinzufuegen(String username, String password, String friend) throws Exception{
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
                json.put("Friend",   friend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/friends/add", json);
        }

        public JSONObject freundLoeschen(String username, String password, String friend) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
                json.put("Friend",   friend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/friends/delete", json);
        }

        public JSONObject getFreundListe(String username, String password) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/friends/get", json);
        }

        public JSONObject getChatHistory(String username, String password, String recipient) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
                json.put("Recipient", recipient);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/message/get", json);
        }

        public JSONObject sendToken(String username, String password, String pushToken ) throws Exception {
            JSONObject messageBody = new JSONObject();

            try {
                messageBody.put("Username", username);
                messageBody.put("Password", password);
                messageBody.put("PushToken", pushToken );
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/user/pushtoken",messageBody);

        }


        public JSONObject sendMsg(String username, String password, String recipient, String data) throws Exception {
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
                json.put("Recipient", recipient);
                json.put("Mimetype", "text/plain");
                json.put("Data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/message/send", json);
        }




}
