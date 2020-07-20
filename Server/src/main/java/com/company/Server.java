package com.company;

public class Server {
    public static void main(String [] args)
    {
        int port = 8888;
        ServerManager serverManager = new ServerManager(port);
        serverManager.start();
    }
}
