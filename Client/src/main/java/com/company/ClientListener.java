package com.company;

public interface ClientListener {
    public void online(String userName);
    public void offline(String userName);
    public void onMessage(String userName, String msg);
}
