package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Login extends JFrame {
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton signupButton = new JButton("Sign Up");

    private final ClientHandle client;

    public Login() throws IOException {
        super("Login");
        setSize(300,200);
        this.client = new ClientHandle("localhost",8888);
        client.connect();



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.add(userTextField);
        jPanel.add(passwordField);
        jPanel.add(loginButton);
        jPanel.add(signupButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendLoginRequest();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        getContentPane().add(jPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void sendLoginRequest() throws IOException {
        String user = userTextField.getText();
        String pass = passwordField.getText();

        if(client.login(user,pass))
        {
            ClientMainGUI clientMainGUI = new ClientMainGUI(client);
            client.handleSocket();

            JFrame jFrame = new JFrame("Chat application");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setSize(480,700);

            jFrame.getContentPane().add(clientMainGUI,BorderLayout.CENTER);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);

            setVisible(false);
        }
        else {
            JOptionPane.showMessageDialog(this,"Please check your user name or password");
        }
    }

    public static void main(String [] args) throws IOException {
        Login login = new Login();
        login.setVisible(true);
    }
}
