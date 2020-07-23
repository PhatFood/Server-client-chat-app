package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class ClientInbox extends JPanel implements ClientListener {
    private final ClientHandle client;
    private final String userName;
    private DefaultListModel<String> listModelInbox = new DefaultListModel<>();
    ;
    private JList<String> jListMessage = new JList<>(listModelInbox);
    private JTextField jTextFieldInput = new JTextField();
    private JButton sendFileBtn = new JButton("Send file");
    private String currentFileName;
    private String currentFile;

    /*public ClientInbox(ClientHandle client, String userName) {
        this.client = client;
        this.userName = userName;
        client.addClientListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(jListMessage), BorderLayout.CENTER);
        add(jTextFieldInput, BorderLayout.PAGE_END);

        jListMessage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1) {
                    String temp = jListMessage.getSelectedValue();
                    System.out.println(temp);
                }
            }
        });

        jTextFieldInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = jTextFieldInput.getText();
                if (msg.length() > 0) {
                    client.sendMessage(userName, msg);
                    listModelInbox.addElement("You: " + msg);
                    jTextFieldInput.setText("");
                }
            }
        });
    }*/

    public ClientInbox(ClientHandle client, String userName, ArrayList<String> arrayList, String fileName) {
        this.client = client;
        this.userName = userName;
        client.addClientListener(this);
        currentFileName = fileName;

        setLayout(new BorderLayout());
        add(new JScrollPane(jListMessage), BorderLayout.CENTER);
        //add(jTextFieldInput, BorderLayout.PAGE_END);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3));
        panel.add(jTextFieldInput, 0, 0);
        panel.add(sendFileBtn, 0, 1);
        //panel.add(new JLabel(""),0,1);
        add(panel, BorderLayout.PAGE_END);

        for (String line : arrayList) {
            listModelInbox.addElement(line);
        }


        jListMessage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String temp = jListMessage.getSelectedValue();
                    if (temp.equals(currentFileName)) {
                        handleReceivingFile();
                    }
                }
            }
        });

        sendFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sendFileEvent();
                JFileChooser fileChooser = new JFileChooser();
                File f = OpenFile(fileChooser);
                if(f != null) {
                    currentFileName = f.getName();
                    currentFile = f.getAbsolutePath() ;
                    client.sendFileRequest(userName, currentFileName);


                    String message = "You send a file:";
                    listModelInbox.addElement(message);
                    String message1 = currentFileName;
                    listModelInbox.addElement(message1);
                }
            }
        });

        jTextFieldInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = jTextFieldInput.getText();
                if (msg.length() > 0) {
                    client.sendMessage(userName, msg);
                    listModelInbox.addElement("You: " + msg);
                    jTextFieldInput.setText("");
                }
            }
        });
    }

    private void handleReceivingFile() {
        currentFileName = "";
        FileReceiverThread fileReceiverThread = new FileReceiverThread(client.getCurrentUser(), userName,8888,"localhost",this);
        fileReceiverThread.start();
    }

    public File OpenFile(JFileChooser fileChooser) {
        fileChooser.setDialogTitle("Pick a file to send");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            return f;
        } else {
            System.out.println("No Selection ");
            return null;
        }
    }

    /*private void sendFileEvent() {
        FileTransferThread fileTransferThread = new FileTransferThread(client.getUserName(),userName,"localhost",8888,"hello.txt");
        fileTransferThread.start();
    }*/

    @Override
    public void online(String userName) {
        if (userName.equals(this.userName)) {
            String message = userName + " is online!";
            listModelInbox.addElement(message);
        }
    }

    @Override
    public void offline(String userName) {
        if (userName.equals(this.userName)) {
            String message = userName + " is offline!";
            listModelInbox.addElement(message);
        }
    }

    @Override
    public void onMessage(String userName, String msg) {
        String message = userName + ": " + msg;
        listModelInbox.addElement(message);
    }

    @Override
    public void onReceivingFile(String userName, String fileName) {
        String message = userName + "send a file, click file name to download:";
        listModelInbox.addElement(message);
        String message1 = fileName;
        currentFileName = fileName;
        listModelInbox.addElement(message1);
    }

    @Override
    public void onReadyToSendFile(String receiveName, String senderName) {
        FileTransferThread fileTransferThread = new FileTransferThread(senderName,receiveName,"localhost",8888,currentFile,this);
        fileTransferThread.start();
        currentFile="";
    }
}
