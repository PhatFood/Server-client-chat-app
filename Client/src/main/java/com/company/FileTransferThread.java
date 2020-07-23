package com.company;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileTransferThread extends Thread {
    private Socket socket;
    private String host;
    private int port;
    private String file;
    private String senderUser;
    private String receiverUser;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private ClientInbox clientInbox;
    private final int BUFFER_SIZE = 8192;

    public FileTransferThread(String sender, String receiver,String host, int port, String file, ClientInbox clientInbox)
    {
        this.clientInbox = clientInbox;
        this.host = host;
        this.senderUser = sender;
        this.port = port;
        this.receiverUser = receiver;
        this.file = file;
    }

    @Override
    public void run() {
        try{
            socket = new Socket(host,port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            File filename = new File(file);
            int len = (int) filename.length();
            int filesize = (int)Math.ceil(len / BUFFER_SIZE);
            String clean_filename = filename.getName();

            String command = "send_File "+ clean_filename.replace(" ", "_") +" "+ filesize +" "+ receiverUser +" "+ senderUser;
            dataOutputStream.writeUTF(command);

            InputStream input = new FileInputStream(filename);
            OutputStream output = socket.getOutputStream();

            BufferedInputStream bis = new BufferedInputStream(input);

            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while((count = bis.read(buffer)) > 0){
                output.write(buffer, 0, count);
            }
            JOptionPane.showMessageDialog(clientInbox, "Send file successful.!");

            output.flush();
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
        }
    }
}
