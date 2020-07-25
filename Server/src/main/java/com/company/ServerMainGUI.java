package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerMainGUI extends JFrame {
    private static DefaultListModel<String> listModelStates = new DefaultListModel<>();
    private static JList<String> serverStates = new JList<>(listModelStates);
    private static JButton startBtn = new JButton("Start server");
    private final int PORT = 8888;

    private ServerMainGUI(){
        super("Server");
        setSize(800,480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        rootPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(new JScrollPane(serverStates),BorderLayout.CENTER);
        add(startBtn,BorderLayout.PAGE_END);
        setLocationRelativeTo(null);

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    ServerManager serverManager = new ServerManager(PORT);
                    serverManager.start();
                    startBtn.setName("Stop server");
                    startBtn.setText("Server is running");
                    startBtn.setEnabled(false);
            }
        });
    }

    public static void addStateMsg(String msg, int level){
        for (int i = 0; i < level; i++){
            msg = "   " + msg;
        }
        listModelStates.addElement(msg);
    }

    public static void main(String[] args)
    {
        ServerMainGUI serverMainGUI = new ServerMainGUI();
        serverMainGUI.setVisible(true);
    }
}
