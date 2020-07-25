package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateTopic extends JFrame {
    private JTextField topicTextField = new JTextField();
    private JButton createButton = new JButton("Create");
    private ClientHandle clientHandle;

    public CreateTopic(ClientHandle clientHandle, ClientMainGUI clientMainGUI){
        super("Create topic");

        this.clientHandle = clientHandle;

        setSize(300,100);
        setLocationRelativeTo(clientMainGUI);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.add(topicTextField);
        jPanel.add(createButton);
        getContentPane().add(jPanel, BorderLayout.CENTER);
        setVisible(true);

        getRootPane().setDefaultButton(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String topicName = topicTextField.getText().replace(" ", "_");
                if (topicName.length() > 0)
                {
                    onCreateTopic(topicName);
                }
            }
        });
    }

    private void onCreateTopic(String topicName) {
        clientHandle.createTopic(topicName);
        this.setVisible(false);
    }
}
