package com.dev.objects;

public class MessageObject {
    private int id;
    private String title;
    private String content;
    private String senderName;
    private boolean messageWasRead;
    private String date;

    //Constructors
    public MessageObject(){ }

    public MessageObject(int id, String title, String content, String senderName, boolean messageWasRead, String date){
        this.setId(id);
        this.setTitle(title);
        this.setContent(content);
        this.setSenderName(senderName);
        this.setMessageWasRead(messageWasRead);
        this.setDate(date);
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getSenderName() { return senderName;}

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() { return title; }

    public boolean isMessageWasRead() { return messageWasRead; }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) { this.title = title; }

    public void setMessageWasRead(boolean messageWasRead) { this.messageWasRead = messageWasRead; }
}
