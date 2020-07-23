package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class Signup extends JFrame{
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton signupButton = new JButton("Sign Up");
    private ClientHandle clientHandle;
    private Login login;

    public Signup(ClientHandle client, Login login) throws IOException {
        super("Sign up");

        this.login = login;
        this.clientHandle = client;
        setSize(300,200);
        setLocationRelativeTo(login);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.add(userTextField);
        jPanel.add(passwordField);
        jPanel.add(signupButton);
        getContentPane().add(jPanel, BorderLayout.CENTER);
        setVisible(true);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                returnLoginScreen();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                login.setVisible(true);
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

                returnLoginScreen();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userTextField.getText();
                String pass = passwordField.getText();

                signUp(user,pass);
            }
        });

    }

    private void returnLoginScreen(){
        login.setVisible(true);
        login.setLocation(this.getLocation());
    }

    private void signUp(String user, String pass) {
        if(clientHandle.signup(user,pass))
        {
            JOptionPane.showMessageDialog(this,"Signup successful");
        }
        else JOptionPane.showMessageDialog(this,"Signup failed! Please check again you username or password");
    }
}
