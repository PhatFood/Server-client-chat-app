package com.company;

import java.io.*;

public class Client {
    public static void main(String[] args) {
        try {
            ClientHandle clientHandle = new ClientHandle("localhost",8888);
            clientHandle.login("guest","1");
            clientHandle.handleSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
