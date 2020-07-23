package com.company;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class FileReceiverThread extends Thread {
    private Socket socket;
    private String host;
    private int port;
    private String filePath;
    private String senderUser;
    private String receiverUser;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final int BUFFER_SIZE = 8192;
    private ClientInbox clientInbox;

    public FileReceiverThread(String senderUser, String receiverUser, int Port, String host, ClientInbox clientInbox) {
        this.host = host;
        this.senderUser = senderUser;
        this.port = Port;
        this.receiverUser = receiverUser;
        this.clientInbox = clientInbox;
    }

    @Override
    public void run() {
        this.filePath = OpenFolder();
        System.out.println(filePath);
        if (filePath == null)
        {
            return;
        }
        try {
            socket = new Socket(host,port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            String command = "readyReceive " + senderUser + " " + receiverUser;
            dataOutputStream.writeUTF(command);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String data=null;
            while (!Thread.currentThread().isInterrupted()) {
                data = dataInputStream.readUTF();
                System.out.println(data);
                String[] tokens = StringUtils.split(data);

                if(tokens[0].equals("sendingFile"))
                {
                        String sender = null;
                        try {
                            System.out.println("Receiving...");
                            String filename = tokens[1];
                            int filesize = Integer.parseInt(tokens[2]);
                            sender = tokens[3]; // Get the Sender Username
                            System.out.println("Đang tải File....");
                            System.out.println("From: " + sender);
                            String path = filePath + filename;

                            FileOutputStream fos = new FileOutputStream(path);
                            fos.flush();
                            InputStream input = socket.getInputStream();

                            BufferedInputStream bIS = new BufferedInputStream(input);
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int count, percent = 0;
                            int test = 0;
                            while((count = bIS.read(buffer)) != -1){
                                fos.write(buffer, 0, count);
                                test++;
                            }
                            System.out.println("test: "+ test);
                            bIS.close();
                            fos.flush();
                            fos.close();
                            this.socket.close();
                            JOptionPane.showMessageDialog(clientInbox, "File saved at: \n'" + path + "'");
                            System.out.println("File saved at: " + path);


                            } catch (IOException e) {
                            e.printStackTrace();

                            System.out.println(e.getMessage());
                            try {
                                socket.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public String OpenFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Pick a folder to save");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if(chooser.showOpenDialog(clientInbox) == JFileChooser.APPROVE_OPTION)
        {
            return chooser.getSelectedFile().getAbsolutePath() +"\\";
        }
        else return null;
    }
}