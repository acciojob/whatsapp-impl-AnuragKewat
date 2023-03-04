package com.driver;

import java.util.Comparator;
import java.util.Date;

public class Message implements Comparator<Message> {
    private int id;
    private String content;
    private Date timestamp;

    public Message(int id, String content, Date timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }
//    public int compareTo(Message other) {
//        return other.getTimestamp().compareTo(this.getTimestamp());
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compare(Message o1, Message o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
