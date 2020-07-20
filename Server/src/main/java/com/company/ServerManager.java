package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerManager extends Thread {
    private final int port;
    private static ArrayList<ServerHandle> clientHandles = new ArrayList<>();
    private static ArrayList<Account> accounts = new ArrayList<>();
    private final String FILE_ACC = "account.xml";

    public ServerManager(int port) {
        this.port = port;
        AccountFileMng.LoadAccounts(accounts,FILE_ACC);
    }

    public static boolean checkAccount(Account account) {
        for (Account a : accounts) {
            if (a.name.equals(account.name)) {
                if (a.pass.equals(account.pass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<ServerHandle> getClientHandles() {
        return clientHandles;
    }

    public static void removeServerHandle(ServerHandle serverHandle) {
        clientHandles.remove(serverHandle);
    }

    public static void addServerHandle(ServerHandle serverHandle)
    {
        clientHandles.add(serverHandle);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for conection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted one connection from " + clientSocket);
                ServerHandle serverHandle = new ServerHandle(clientSocket);
                //serverHandles.add(serverHandle);
                serverHandle.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
