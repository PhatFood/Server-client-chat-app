package com.company;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class ClientInbox extends JPanel implements ClientListener {
    private final ClientHandle client;
    private final String userName;
    private DefaultListModel<String> listModelInbox = new DefaultListModel<>();

    private JList<String> jListMessage = new JList<>(listModelInbox);
    private JTextField jTextFieldInput = new JTextField();
    private JButton sendFileBtn = new JButton("Send file");
    private JButton emojiBtn = new JButton(EmojiParser.parseToUnicode(":smile:"));
    private String currentFileName;
    private String currentFile;

    private boolean isGroupChat;

    public ClientInbox(ClientHandle client, String userName, ArrayList<String> arrayList, String fileName, boolean isGroupChat) {
        this.client = client;
        this.userName = userName;
        client.addClientListener(this);
        currentFileName = fileName;
        this.isGroupChat = isGroupChat;
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(new JScrollPane(jListMessage), BorderLayout.CENTER);
        //add(jTextFieldInput, BorderLayout.PAGE_END);


        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setLayout(new BorderLayout());

        panel.add(jTextFieldInput, BorderLayout.CENTER);

        if (!isGroupChat) {
            JPanel jPanelBtn = new JPanel();
            jPanelBtn.setLayout(new BorderLayout());
            jPanelBtn.add(sendFileBtn,BorderLayout.CENTER);
            jPanelBtn.add(emojiBtn,BorderLayout.PAGE_START);

            panel.add(jPanelBtn, BorderLayout.EAST);

            sendFileBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    File f = OpenFile(fileChooser);
                    if (f != null) {
                        currentFileName = f.getName();
                        currentFile = f.getAbsolutePath();
                        client.sendFileRequest(userName, currentFileName);

                        String message = "You send a file:";
                        listModelInbox.addElement(message);
                        String message1 = currentFileName;
                        listModelInbox.addElement(message1);
                    }
                }
            });
        }
        else {
            panel.add(emojiBtn,BorderLayout.EAST);
        }

        emojiBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pickEmoji();
            }
        });

        //panel.add(new JLabel(""),0,1);
        add(panel, BorderLayout.PAGE_END);

        if (!isGroupChat) {
            for (String line : arrayList) {
                listModelInbox.addElement(line);
            }
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


        jTextFieldInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = jTextFieldInput.getText();
                if (msg.length() > 0) {
                    client.sendMessage(userName, msg);
                    String emoM = EmojiParser.parseToUnicode(msg);
                    listModelInbox.addElement("You: " + emoM);
                    jTextFieldInput.setText("");
                }
            }
        });
    }

    private void pickEmoji() {
        emojiBtn.setEnabled(false);
        EmojiWindow emojiWindow = new EmojiWindow(this,jTextFieldInput);
        emojiWindow.setVisible(true);
        emojiWindow.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                emojiBtn.setEnabled(true);
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

    private void handleReceivingFile() {
        currentFileName = "";
        FileReceiverThread fileReceiverThread = new FileReceiverThread(client.getCurrentUser(), userName, 8888, "localhost", this);
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

        String name;

        if (isGroupChat && userName.charAt(0) == '*') {
            String topicName = StringUtils.split(userName, '/')[0];
            if (topicName.equals(this.userName)) {
                System.out.println(userName);
                name = StringUtils.split(userName, '/')[1];
                String message = name + ": " + msg;
                String emoM = EmojiParser.parseToUnicode(message);
                listModelInbox.addElement(emoM);
            }
        } else if (!isGroupChat && userName.charAt(0) != '*' && userName.equals(this.userName)) {
            name = userName;
            String message = name + ": " + msg;
            String emoM = EmojiParser.parseToUnicode(message);
            listModelInbox.addElement(emoM);
        }
    }

    @Override
    public void onReceivingFile(String userName, String fileName) {
        if (!isGroupChat && userName.equals(this.userName)) {
            String message = userName + " send a file, click file name to download:";
            listModelInbox.addElement(message);
            String message1 = fileName;
            currentFileName = fileName;
            listModelInbox.addElement(message1);
        }
    }

    @Override
    public void onReadyToSendFile(String receiveName, String senderName) {
        FileTransferThread fileTransferThread = new FileTransferThread(senderName, receiveName, "localhost", 8888, currentFile, this);
        fileTransferThread.start();
        currentFile = "";
    }
}
