package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandle extends Thread {

    private Socket clientSocket;
    private Account account;
    private boolean isJoined;
    private BufferedWriter bw;

    public ServerHandle(Socket clientSocket) {
        this.clientSocket = clientSocket;
        bw = null;
        isJoined = false;
        account = null;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            ServerManager.removeServerHandle(this);
            notifyOffline();
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) throws IOException {
        if (bw == null) {
            return;
        }
        if (isJoined) {
            bw.write(msg);
            bw.newLine();
            bw.flush();
        }
    }

    public void handleClient() throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        bw = new BufferedWriter(new OutputStreamWriter(outputStream));
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String receivedMessage;

        try {
            do {
                receivedMessage = br.readLine();
                System.out.println("Received : " + receivedMessage);

                String[] tokens = StringUtils.split(receivedMessage);
                if (tokens != null && tokens.length > 0) {
                    if (tokens[0].equals("login")) {
                        isJoined = handleLogin(tokens);
                        if (isJoined) {
                            handleOnlineEvent();
                        } else handleLoginFailedEvent();
                    } else if (tokens[0].equals("message")) {
                        handleMessageSingle(receivedMessage);
                    } else if (tokens[0].equals("logout")) {
                        handleOfflineEvent();
                        break;
                    } else {
                        sendMsg("unknow");
                    }
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            notifyOffline();
            ServerManager.removeServerHandle(this);
            clientSocket.close();
            isJoined = false;
            bw.close();
            br.close();
        }
    }

    private void handleMessageSingle(String receivedMessage) {
        String tokens[] = StringUtils.split(receivedMessage, null, 3);
        String name = tokens[1];
        String message = tokens[2];

        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != null && serverHandle.getUserName().equals(name)) {
                try {
                    serverHandle.sendMsg("message " + this.account.name + " " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getUserName() {
        if (account != null)
            return account.name;
        return null;
    }

    private void sendClientCurrentOnline() {
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != null && serverHandle.getUserName() != this.account.name) {
                try {
                    sendMsg("isonline " + serverHandle.getUserName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendClientCurrentOffline() {
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != null && serverHandle.getUserName() != this.account.name) {
                try {
                    sendMsg("isoffline " + serverHandle.getUserName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleOfflineEvent() throws IOException {
        System.out.println("Client has left");
        notifyOffline();
        sendMsg("disconnected");
        sendClientCurrentOffline();
        ServerManager.removeServerHandle(this);
    }

    private void handleLoginFailedEvent() throws IOException {
        if (bw == null) {
            return;
        }
        bw.write("loginFailed");
        bw.newLine();
        bw.flush();
    }

    private void handleOnlineEvent() throws IOException {
        sendMsg("logsuccess");
        notifyOnline();
        sendClientCurrentOnline();
        ServerManager.addServerHandle(this);
    }

    private void notifyOffline() {
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != this.account.name) {
                try {
                    serverHandle.sendMsg("isoffline " + account.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void notifyOnline() {
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != this.account.name) {
                try {
                    serverHandle.sendMsg("isonline " + account.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean handleLogin(String[] tokens) {
        if (tokens.length == 3) {
            Account tempA = new Account(tokens[1], tokens[2]);
            if (ServerManager.checkAccount(tempA)) {
                this.account = tempA;
                return true;
            }
        }
        return false;
    }
}
