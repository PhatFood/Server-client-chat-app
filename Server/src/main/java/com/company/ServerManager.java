package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerManager extends Thread {
    private final int port;
    private static ArrayList<ServerHandle> clientHandles = new ArrayList<>();
    private static ArrayList<ServerHandle> fileTransferHandles = new ArrayList<>();
    private static ArrayList<Account> accounts = new ArrayList<>();

    public ServerManager(int port) {
        this.port = port;
        AccountFileMng.LoadAccounts(accounts);
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

    public static ArrayList<ServerHandle> getFileTransferHandles() {
        return fileTransferHandles;
    }

    public static ServerHandle getServerHandleReceiver(String sendingUser, String receivingUser)
    {
        for(ServerHandle serverHandle: fileTransferHandles)
        {
            if (serverHandle.getSendingName().equals(sendingUser) && serverHandle.getReceivingName().equals(receivingUser))
            {
                return serverHandle;
            }
        }
        return null;
    }

    public static Socket getSocketReceiver(String sendingUser, String receivingUser) throws IOException {
        for(ServerHandle serverHandle: fileTransferHandles)
        {
            if (serverHandle.getSendingName().equals(sendingUser) && serverHandle.getReceivingName().equals(receivingUser))
            {
                return serverHandle.getSocket();
            }
        }
        return null;
    }

    public static void removeFileTransferHandle(ServerHandle serverHandle)
    {
        fileTransferHandles.remove(serverHandle);
    }

    public static void addFileTransferHandle(ServerHandle fileTransferHandle) {
        fileTransferHandles.add(fileTransferHandle);
    }


    public static void removeClientHandle(ServerHandle serverHandle) {
        clientHandles.remove(serverHandle);
    }

    public static void addClientHandle(ServerHandle serverHandle)
    {
        clientHandles.add(serverHandle);
    }

    public static boolean addAccount(Account account) {
        for (Account a : accounts) {
            if (a.name.equals(account.name)) {
                    return false;
            }
        }
        if (AccountFileMng.AddAccount(account)){
            accounts.add(account);
            return true;
        }
        return false;
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
                serverHandle.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
