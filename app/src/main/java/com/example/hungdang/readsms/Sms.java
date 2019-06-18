package com.example.hungdang.readsms;

public class Sms {
    private String Number;
    private String Message;

    public Sms() {

    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Sms(String number, String message) {

        Number = number;
        Message = message;
    }
}
