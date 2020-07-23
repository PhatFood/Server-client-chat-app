package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandle extends Thread {

    private final Socket socket;
    private Account account;
    private boolean isUserJoined;
    private boolean isFileTransferSocket;
    private String receiverName;
    private String senderName;
    private final int BUFFER_SIZE = 8192;

    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    public ServerHandle(Socket socket) throws IOException {
        this.socket = socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        isUserJoined = false;
        isFileTransferSocket = false;
        account = null;
        receiverName = null;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            /*ServerManager.removeClientHandle(this);
            notifyOffline();
            e.printStackTrace();*/
            e.printStackTrace();

            if (isFileTransferSocket) {
                ServerManager.removeFileTransferHandle(this);
            } else {
                notifyOffline();
                ServerManager.removeClientHandle(this);
                isUserJoined = false;
               /* try {
                    socket.close();
                } catch (IOException ioException) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public void sendMsg(String msg) throws IOException {
        if (isUserJoined) {
            dataOutputStream.writeUTF(msg);
        }
    }

    public void handleClient() throws IOException {
        String receivedMessage;

        try {
            do {
                receivedMessage = dataInputStream.readUTF();
                System.out.println("Received : " + receivedMessage);

                String[] tokens = StringUtils.split(receivedMessage);
                if (tokens != null && tokens.length > 0) {
                    if (tokens[0].equals("login")) {
                        isUserJoined = handleLogin(tokens);
                        if (isUserJoined) {
                            handleOnlineEvent();
                        } else handleLoginFailedEvent();
                    } else if(tokens[0].equals("signup")){
                        handleSignup(tokens);
                    }
                    else if (tokens[0].equals("message")) {
                        handleMessageSingle(receivedMessage);
                    } else if (tokens[0].equals("requestSendFile")) {
                        System.out.println(ServerManager.getFileTransferHandles().size());
                        handleRequestTransferFile(tokens);
                    } else if (tokens[0].equals("readyReceive")) {
                        System.out.println(ServerManager.getFileTransferHandles().size());
                        handleReadyReceiverFile(tokens);
                        break;
                    } else if (tokens[0].equals("send_File")){
                        System.out.println(ServerManager.getFileTransferHandles().size());
                        readingFileAndSentToClient(tokens);
                        break;
                    } else if (tokens[0].equals("fQuit")) {
                        break;
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
            if (isFileTransferSocket) {
                //ServerManager.removeFileTransferHandle(this);
            } else {
                notifyOffline();
                ServerManager.removeClientHandle(this);
                isUserJoined = false;
                //socket.close();
            }
        }
    }

    private void readingFileAndSentToClient(String[] tokens) throws IOException {
        isFileTransferSocket = true;
        String file_name = tokens[1];
        String filesize = tokens[2];

        String receiver = tokens[4];
        String sender = tokens[3];

        Socket receiverSocket = ServerManager.getSocketReceiver(sender,receiver);

        if (receiverSocket != null) {
            try {
                System.out.println("Sending.......");
                String cmd = "sendingFile " + file_name + " " + filesize + " " + sender;
                System.out.println(cmd);
                DataOutputStream dataOutputStreamR = new DataOutputStream(receiverSocket.getOutputStream());
                dataOutputStreamR.writeUTF(cmd);

                InputStream input = socket.getInputStream();
                OutputStream sendFile = receiverSocket.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int cnt;
                int test = 0;
                while ((cnt = input.read(buffer)) > 0) {
                    test++;
                    sendFile.write(buffer, 0, cnt);
                }
                System.out.println("test " + test);
                sendFile.flush();
                sendFile.close();
                //this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleReadyReceiverFile(String[] tokens) {
        isFileTransferSocket = true;
        receiverName = tokens[1];
        senderName = tokens[2];
        ServerManager.addFileTransferHandle(this);

        String senderName = tokens[1];
        String receiverName = tokens[2];
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != null && serverHandle.getUserName().equals(receiverName)) {
                try {
                    serverHandle.sendMsg("acceptedSendFile " + receiverName + " " + senderName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleRequestTransferFile(String[] tokens) {
        String receiverName = tokens[1];
        ArrayList<ServerHandle> serverHandles = ServerManager.getClientHandles();
        for (ServerHandle serverHandle : serverHandles) {
            if (serverHandle.getUserName() != null && serverHandle.getUserName().equals(receiverName)) {
                try {
                    serverHandle.sendMsg("sendingFileRequest " + this.account.name + " " + tokens[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        ServerManager.removeClientHandle(this);
    }

    private void handleLoginFailedEvent() throws IOException {
        dataOutputStream.writeUTF("loginFailed");
    }

    private void handleOnlineEvent() throws IOException {
        sendMsg("logsuccess");
        notifyOnline();
        sendClientCurrentOnline();
        ServerManager.addClientHandle(this);
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


    private void handleSignup(String[] tokens) throws IOException {
        if(tokens.length == 3){
            Account tempA = new Account(tokens[1], tokens[2]);
            if (ServerManager.addAccount(tempA)) {
                    dataOutputStream.writeUTF("signupsuccess");
                    return;
            }
            else {
                dataOutputStream.writeUTF("signupsuccess");
            }
            return;
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

    public String getSendingName() {
        return senderName;
    }

    public String getReceivingName() {
        return receiverName;
    }

    public Socket getSocket() throws IOException {
        ServerManager.removeFileTransferHandle(this);
        return socket;
    }
}
