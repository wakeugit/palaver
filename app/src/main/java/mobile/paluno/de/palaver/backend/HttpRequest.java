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

        public JSONObject verbindungHerstellen(String _url, String username, String password) throws Exception{
            //Benutzer in JSON Form anlegen und Passwort übergeben
            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/user/register",
                    username, password);
        }

        public JSONObject benutzerValidate(String username, String password) throws Exception{
            return verbindungHerstellen("http://palaver.se.paluno.uni-due.de/api/user/validate",
                    username, password);
         }

        public JSONObject freundVerwalten(String _url, String username, String password, String friend) throws Exception{

            JSONObject json = new JSONObject();

            try {
                json.put("Username", username);
                json.put("Password", password);
                if (friend!=null) {
                    json.put("Friend", friend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

        public JSONObject freundHinzufuegen(String username, String password, String friend) throws Exception{
            return freundVerwalten("http://palaver.se.paluno.uni-due.de/api/friends/add", username, password, friend);
        }

        public JSONObject freundLoeschen(String username, String password, String friend) throws Exception{
            return freundVerwalten("http://palaver.se.paluno.uni-due.de/api/friends/delete", username, password, friend);
        }

        public JSONObject getFreundListe(String username, String password) throws Exception{
            return freundVerwalten("http://palaver.se.paluno.uni-due.de/api/friends/get", username, password, null);
        }





}
