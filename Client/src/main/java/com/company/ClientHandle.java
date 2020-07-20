package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandle {
    private final Socket socket;
    private boolean isConnected;
    private ArrayList<ClientListener> clientListeners = new ArrayList<>();

    private BufferedWriter bw;
    private BufferedReader br;

    public ArrayList<ClientListener> getLis(){
        return clientListeners;
    }

    ClientHandle(String host, int port) throws IOException {
        final Socket socket = new Socket(host, port);
        this.socket = socket;
        bw = null;
        br = null;
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
        });
        isConnected = false;
    }

    public boolean sendMessage(String userName, String msg) {
        if (bw == null)
            return false;
        try {
            String sentMessage = "";
            sentMessage = "message " + userName + " " + msg;
            bw.write(sentMessage);
            bw.newLine();
            bw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String userName, String passWord) {
        if (bw == null)
            return false;
        try {
            String sentMessage = "";
            sentMessage = "login " + userName + " " + passWord;
            bw.write(sentMessage);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            String receivedMessage;
            receivedMessage = br.readLine();
            String[] tokens = StringUtils.split(receivedMessage);
            if (tokens != null && tokens.length > 0) {
                if (tokens[0].equals("logsuccess")) {
                    return true;
                }
                else if(tokens[0].equals("loginFailed"))
                    return false;
            }
            else return false;
        } catch (IOException e) {
            System.out.println("aaa");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean connect()  {
        try {
            OutputStream os = socket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            InputStream is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendFile(String file, String userName)
    {
        if (bw == null)
            return false;
        try {
            String sentMessage = "";
            sentMessage = "file "+userName;
            bw.write(sentMessage);
            bw.newLine();
            bw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void handleSocket() throws IOException {

        /*Thread t1, t2, t3;
        t1 = new Thread(() -> sendRequestToServer());
        //t1.start();*/

        Thread t1;

        t1 = new Thread(() -> receiveRequestFromServer());
        t1.start();

        /*t3 = new Thread(){
            @Override
            public void run() {
                try {
                    while (true) {
                        if (!t1.isAlive() && !t2.isAlive()) {
                            br.close();
                            bw.close();
                            socket.close();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            };
        t3.start();*/


    }

    private void sendRequestToServer() {
        try {
            String sentMessage = "";
            do {
                DataInputStream din = new DataInputStream(System.in);
                sentMessage = din.readLine();
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();
                if (sentMessage.equalsIgnoreCase("logout")) {
                    br.close();
                    bw.close();
                    socket.close();
                    break;
                }
            }
            while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveRequestFromServer() {
        try {
            String receivedMessage = null;
            do {
                receivedMessage = br.readLine();
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
                    }
                } else {
                    break;
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
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
}