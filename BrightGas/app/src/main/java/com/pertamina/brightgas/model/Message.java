package com.pertamina.brightgas.model;

public class Message {

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;

    public String time;
    public String message;
    public int type;

    public Message(String time, String message, int type) {
        this.time = time;
        this.message = message;
        this.type = type;
    }

}
