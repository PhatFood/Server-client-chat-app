package com.company.GUI;

import com.company.ClientHandle;
import com.company.ClientListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainGUI extends JPanel implements ClientListener, WindowListener {

    private final ClientHandle client;
    private JList<String> jListClientOnline;
    private DefaultListModel<String> clientListModel;
    private JList<String> jListTopics;
    private DefaultListModel<String> topicsListModel;
    private HashMap<String, ArrayList<String>> waitingMsg = new HashMap<>();
    private HashMap<String, String> waitingFile = new HashMap<>();
    private HashSet<String> openingInbox = new HashSet<>();
    private JButton createTopicBtn = new JButton("Create new topic");

    public MainGUI(ClientHandle client) {
        this.client = client;
        this.client.addClientListener(this);
        clientListModel = new DefaultListModel<>();
        jListClientOnline = new JList<>(clientListModel);
        topicsListModel = new DefaultListModel<>();
        jListTopics = new JList<>(topicsListModel);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 0));

        JPanel panelUsers = new JPanel();
        panelUsers.setLayout(new BorderLayout());
        panelUsers.add(new JScrollPane(jListClientOnline),BorderLayout.CENTER);
        panelUsers.add(new JLabel("User: "),BorderLayout.PAGE_START);

        JPanel panelTopics = new JPanel();
        panelTopics.setLayout(new BorderLayout());
        panelTopics.add(new JScrollPane(jListTopics),BorderLayout.CENTER);
        panelTopics.add(new JLabel("Topics: "),BorderLayout.PAGE_START);

        panel.add(panelUsers, 0);
        panel.add(panelTopics, 1);
        add(panel,BorderLayout.CENTER);
        add(createTopicBtn, BorderLayout.PAGE_END);

        setBorder(new EmptyBorder(10,10,10,10));

        createTopicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateTopic();
            }
        });

        jListTopics.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String topicName = jListTopics.getSelectedValue();
                    String topicFullName = "*" + topicName;

                    if (!openingInbox.contains(topicFullName)) {
                        client.joinTopic(topicFullName);
                        openingInbox.add(topicFullName);
                        InboxGUI inboxGUI;
                        inboxGUI = new InboxGUI(client, topicFullName, waitingMsg.get(topicFullName), waitingFile.get(topicFullName), true);

                        JFrame jFrame = new JFrame("You joined topic : " + topicName);
                        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        jFrame.setSize(400, 300);
                        jFrame.getContentPane().add(inboxGUI, BorderLayout.CENTER);
                        jFrame.setLocationRelativeTo(null);
                        jFrame.setVisible(true);
                        jFrame.addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                openingInbox.remove(topicFullName);
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

        jListClientOnline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String userName = jListClientOnline.getSelectedValue();

                    if (!openingInbox.contains(userName)) {
                        openingInbox.add(userName);
                        InboxGUI inboxGUI;
                        inboxGUI = new InboxGUI(client, userName, waitingMsg.get(userName), waitingFile.get(userName), false);

                        waitingMsg.get(userName).clear();
                        waitingFile.put(userName, "");

                        JFrame jFrame = new JFrame(userName);
                        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        jFrame.setSize(400, 300);
                        jFrame.getContentPane().add(inboxGUI, BorderLayout.CENTER);
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

    private void onCreateTopic() {
        CreateTopicGUI createTopicGUI = new CreateTopicGUI(client, this);
        createTopicGUI.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            ClientHandle client = new ClientHandle("localhost", 8888);

            client.login("guest", "1");

            MainGUI mainGUI = new MainGUI(client);

            JFrame jFrame = new JFrame("Chat application");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setSize(480, 700);

            jFrame.getContentPane().add(mainGUI, BorderLayout.CENTER);

            jFrame.setVisible(true);
            client.handleSocket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String charRemoveAt(String str, int p) {
        return str.substring(0, p) + str.substring(p + 1);
    }

    @Override
    public void online(String userName) {
        if (userName.charAt(0) == '*') {
            String temp = charRemoveAt(userName,0);
            System.out.println(temp);
            topicsListModel.addElement(temp);
        } else {
            clientListModel.addElement(userName);
            waitingMsg.put(userName, new ArrayList<String>());
        }
    }

    @Override
    public void offline(String userName) {
        if (userName.charAt(0) == '*') {
            topicsListModel.removeElement(userName);
        } else {
            clientListModel.removeElement(userName);
            waitingMsg.remove(userName);
            waitingFile.remove(userName);
        }
    }

    @Override
    public void onMessage(String userName, String msg) {
        if (waitingMsg.containsKey(userName)) {
            waitingMsg.get(userName).add(userName + ": " + msg);
        }
    }

    @Override
    public void onReceivingFile(String userName, String fileName) {
        if (waitingMsg.containsKey(userName)) {
            String message = userName + "sending a file:";
            waitingMsg.get(userName).add(message);
            String message1 = fileName;
            waitingMsg.get(userName).add(message1);
            waitingFile.put(userName, fileName);
        }
    }

    @Override
    public void onReadyToSendFile(String userName, String fileName) {

    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            client.terminate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
}
