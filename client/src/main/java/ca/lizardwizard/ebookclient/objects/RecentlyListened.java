package ca.lizardwizard.ebookclient.objects;

import com.google.gson.annotations.SerializedName;

public class RecentlyListened {
    @SerializedName("id")
    private int id;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("date")
    private String date;
    @SerializedName("book_id")
    private int bookId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
