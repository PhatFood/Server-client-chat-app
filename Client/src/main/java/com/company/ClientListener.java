package com.company;

public interface ClientListener {
    public void online(String userName);
    public void offline(String userName);
    public void onMessage(String userName, String msg);
    public void onReceivingFile(String userName, String fileName);
    public void onReadyToSendFile(String userName, String fileName);
}
