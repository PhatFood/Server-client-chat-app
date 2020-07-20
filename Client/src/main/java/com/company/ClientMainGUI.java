package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClientMainGUI extends JPanel implements ClientListener {

    private final ClientHandle client;
    private JList<String> jListClientOnline;
    private DefaultListModel<String> clientListModel;
    private HashMap<String, ArrayList<String>> waitingMsg = new HashMap<>();
    private HashSet<String> openingInbox = new HashSet<>();

    public ClientMainGUI(ClientHandle client){
        this.client = client;
        this.client.addClientListener(this);
        clientListModel = new DefaultListModel<>();
        jListClientOnline = new JList<>(clientListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(jListClientOnline),BorderLayout.CENTER);

        jListClientOnline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1){
                    String userName = jListClientOnline.getSelectedValue();
                    if(!openingInbox.contains(userName)) {
                        openingInbox.add(userName);
                        ClientInbox clientInbox;
                        clientInbox = new ClientInbox(client,userName,waitingMsg.get(userName));

                        waitingMsg.get(userName).clear();

                        JFrame jFrame = new JFrame(userName);
                        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        jFrame.setSize(400, 300);
                        jFrame.getContentPane().add(clientInbox, BorderLayout.CENTER);
                        jFrame.setLocationRelativeTo(null);
                        jFrame.setVisible(true);
                        jFrame.addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e) {

                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                openingInbox.remove(userName);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {

                            }

                            @Override
                            public void windowIconified(WindowEvent e) {

                            }

                            @Override
                            public void windowDeiconified(WindowEvent e) {

                            }

                            @Override
                            public void windowActivated(WindowEvent e) {

                            }

                            @Override
                            public void windowDeactivated(WindowEvent e) {

                            }
                        });
                    }
                }
            }
        });
    }

    public static void main(String[] args){
        try {
            ClientHandle client = new ClientHandle("localhost",8888);

            client.connect();
            client.login("guest","1");

            ClientMainGUI clientMainGUI = new ClientMainGUI(client);

            JFrame jFrame = new JFrame("Chat application");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setSize(480,700);

            jFrame.getContentPane().add(clientMainGUI,BorderLayout.CENTER);

            jFrame.setVisible(true);
            client.handleSocket();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void online(String userName) {
        clientListModel.addElement(userName);
        waitingMsg.put(userName,new ArrayList<String>());
    }

    @Override
    public void offline(String userName) {
        clientListModel.removeElement(userName);
        waitingMsg.remove(userName);
    }

    @Override
    public void onMessage(String userName, String msg) {
        if(waitingMsg.containsKey(userName)) {
            waitingMsg.get(userName).add(userName + ": " + msg);
            /*int numberMeswait = waitingMsg.get(userName).size();
            if(numberMeswait>0)
            {
                for(int i = 0; i < clientListModel.getSize(); i++)
                {
                    if (clientListModel.getElementAt(i).equals(userName))
                    {
                        clientListModel.getElementAt(i).
                    }
                }
            }*/
        }
    }
}
