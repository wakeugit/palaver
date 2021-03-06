package mobile.paluno.de.palaver.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by wilfried on 27.06.17.
 */

public class Message {
    private String sender;
    private String recipient;
    private String mimeType;
    private String data;
    private String dateTime;
    private boolean isMine;

    public Message(String sender, String recipient, String data, boolean isMine) {
        this.sender = sender;
        this.recipient = recipient;
//        this.mimeType = mimeType;
        this.data = data;
//        this.dateTime = dateTime;
        this.isMine = isMine;
    }

    public Message(String sender, String recipient, String data, boolean isMine, String dateTime) {
        this.sender = sender;
        this.recipient = recipient;
//        this.mimeType = mimeType;
        this.data = data;
        SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        inFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date inDate = null;
        try {
            inDate = inFormatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        outFormatter.setTimeZone(TimeZone.getTimeZone("UTC+01:00"));
        this.dateTime = outFormatter.format(inDate);
        this.isMine = isMine;
        System.out.println("date time : " + this.dateTime);
    }

    public Message(boolean mine){
        /*Default Constructor for testing */
        this.sender = "Eiskalt";
        this.recipient = "Mensa";
        this.mimeType = "text";
        this.data = "Hallo, das ist ein Test aklsjd laksdj l aslkj aslkd jl aksdj lad lkasjd";
        this.dateTime = "27.06.2017 14:30";
        this.isMine = mine;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReciepient(String reciepient) {
        this.recipient = reciepient;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getSender() {
        return sender;
    }

    public String getReciepient() {
        return recipient;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getData() {
        return data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isMine() {
        return isMine;
    }
}
