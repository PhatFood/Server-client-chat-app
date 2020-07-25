package com.company;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandle {
    private Socket socket;

    private String currentUser;
    private ArrayList<ClientListener> clientListeners = new ArrayList<>();

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private volatile boolean running = true;

    public ArrayList<ClientListener> getLis(){
        return clientListeners;
    }

    ClientHandle(String host, int port) throws IOException {
        final Socket socket = new Socket(host, port);
        this.socket = socket;
        currentUser = null;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        addClientListener(new ClientListener() {
            @Override
            public void online(String userName) {
                System.out.println("isonline " + userName);
            }

            @Override
            public void offline(String userName) {
                System.out.println("isoffline " + userName);
            }

            @Override
            public void onMessage(String userName, String msg) {
                System.out.println("receive " + userName + " " + msg);
            }

            @Override
            public void onReceivingFile(String userName, String fileName) {
                System.out.println("receive file "+ userName + " " + fileName);
            }

            @Override
            public void onReadyToSendFile(String userName, String fileName) {
                System.out.println("ready to send file");
            }
        });
    }

    public boolean sendFileRequest(String receiverName, String fileName) {

            String sentMessage = "";
            sentMessage = "requestSendFile " + receiverName+" "+fileName;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendMessage(String userName, String msg) {
            String sentMessage = "";
            sentMessage = "message " + userName + " " + msg;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean joinTopic(String topicName){
        String sentMessage = "";
        sentMessage = "joinT " + topicName;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean createTopic(String topicName) {
        String sentMessage = "";
        sentMessage = "createT " + "*" + topicName;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendMessage(String userName, String msg, String topic) {
        String sentMessage = "";
        sentMessage = "message " + topic + "/" +userName + " " + msg;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean login(String userName, String passWord) {

        String sentMessage = sentMessage = "login " + userName + " " + passWord;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
        try {
            String receivedMessage;
            receivedMessage = dataInputStream.readUTF();
            String[] tokens = StringUtils.split(receivedMessage);
            if (tokens != null && tokens.length > 0) {
                if (tokens[0].equals("logsuccess")) {
                    currentUser = userName;
                    return true;
                }
                else if(tokens[0].equals("loginFailed"))
                    return false;
            }
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    public void handleSocket()  {
        Thread t1;
        t1 = new Thread(this::receiveRequestFromServer);
        t1.start();
    }


    private void receiveRequestFromServer() {
        try {
            String receivedMessage = null;
            while (running)
            {
                receivedMessage = dataInputStream.readUTF();
                System.out.println(receivedMessage);
                String[] tokens = StringUtils.split(receivedMessage);
                if (tokens != null && tokens.length > 0) {
                    if (tokens[0].equals("disconnected")) {
                        break;
                    } else if (tokens[0].equals("message")) {
                        String[] tokens1 = StringUtils.split(receivedMessage, null, 3);
                        handleClientMessage(tokens1[1], tokens1[2]);
                    } else if (tokens[0].equals("isonline")) {
                        handleClientOnline(tokens[1]);
                    } else if (tokens[0].equals("isoffline")) {
                        handleClientOffline(tokens[1]);
                    } else if (tokens[0].equals("sendingFileRequest")){
                        handleIncomingFileRequest(tokens[1], tokens[2]);
                    } else if (tokens[0].equals("acceptedSendFile")){
                        handleReadyToSendFile(tokens[1],tokens[2]);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleReadyToSendFile(String receiveName, String senderName) {
        for(ClientListener clientListener : clientListeners){
            try{
                clientListener.onReadyToSendFile(receiveName,senderName);
            } finally {

            }
        }
    }

    private void handleIncomingFileRequest(String senderName, String fileName) {
        for(ClientListener clientListener : clientListeners){
            try{
                clientListener.onReceivingFile(senderName,fileName);
            } finally {

            }
        }
    }

    private void handleClientMessage(String user, String message) {
        for (ClientListener clientListener : clientListeners) {
            try {
                clientListener.onMessage(user, message);
            } finally {

            }
        }
    }

    private void handleClientOffline(String userName) {
        for (ClientListener clientListener : clientListeners) {
            clientListener.offline(userName);
        }
    }

    private void handleClientOnline(String userName) {
        for (ClientListener clientListener : clientListeners) {
            clientListener.online(userName);
        }
    }

    public void addClientListener(ClientListener listener) {
        clientListeners.add(listener);
    }

    public void removeClientListener(ClientListener listener) {
        clientListeners.remove(listener);
    }


    public void terminate() {
        try {
            running = false;
            dataOutputStream.writeUTF("logout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean signup(String userName, String passWord) {
        String sentMessage = "signup " + userName + " " + passWord;
        try {
            dataOutputStream.writeUTF(sentMessage);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
        try {
            String receivedMessage;
            receivedMessage = dataInputStream.readUTF();
            System.out.println(receivedMessage);
            String[] tokens = StringUtils.split(receivedMessage);
            if (tokens != null && tokens.length > 0) {
                if (tokens[0].equals("signupsuccess")) {
                    currentUser = userName;
                    return true;
                }
                else if(tokens[0].equals("signupfailed"))
                    return false;
            }
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}