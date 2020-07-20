package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientInbox extends JPanel implements ClientListener {
    private final ClientHandle client;
    private final String userName;
    private DefaultListModel<String> listModelInbox= new DefaultListModel<>(); ;
    private JList<String> jListMessage = new JList<>(listModelInbox);
    private JTextField jTextFieldInput = new JTextField();
    private JButton sendFileBtn = new JButton("Send file");

    public ClientInbox(ClientHandle client, String userName) {
        this.client = client;
        this.userName = userName;
        client.addClientListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(jListMessage), BorderLayout.CENTER);
        add(jTextFieldInput, BorderLayout.PAGE_END);

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

    public ClientInbox(ClientHandle client, String userName, ArrayList<String> arrayList) {
        this.client = client;
        this.userName = userName;
        client.addClientListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(jListMessage), BorderLayout.CENTER);
        //add(jTextFieldInput, BorderLayout.PAGE_END);


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,3));
        panel.add(jTextFieldInput, 0, 0);
        panel.add(sendFileBtn, 0, 1);
        //panel.add(new JLabel(""),0,1);
        add(panel,BorderLayout.PAGE_END);

        for(String line : arrayList)
        {
            listModelInbox.addElement(line);
        }

        sendFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFileEvent();
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

    private void sendFileEvent() {
        client.sendFile("account.xml",userName);
    }

    @Override
    public void online(String userName) {
        String message = userName + " is online!";
        listModelInbox.addElement(message);
    }

    @Override
    public void offline(String userName) {
        String message = userName + " is offline!";
        listModelInbox.addElement(message);
    }

    @Override
    public void onMessage(String userName, String msg) {
        String message = userName + ": " + msg;
        listModelInbox.addElement(message);
    }
}
