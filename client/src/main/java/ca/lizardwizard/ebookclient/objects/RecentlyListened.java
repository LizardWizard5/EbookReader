package ca.lizardwizard.ebookclient.objects;

import com.google.gson.annotations.SerializedName;

public class RecentlyListened {
    @SerializedName("id")
    private int id;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("date")
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
