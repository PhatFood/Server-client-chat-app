package com.company;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends JFrame{
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel userNameLabel = new JLabel("User name: ");
    private JLabel passWordLabel = new JLabel("Password: ");
    private JButton signupButton = new JButton("Sign Up");
    private ClientHandle clientHandle;
    private Login login;

    public Signup(ClientHandle client, Login login) throws IOException {
        super("Sign up");

        this.login = login;
        this.clientHandle = client;
        setSize(login.getWidth(),login.getHeight());
        setLocationRelativeTo(login);

        setResizable(false);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(6,0));
        jPanel.add(userNameLabel);
        jPanel.add(userTextField);
        jPanel.add(passWordLabel);
        jPanel.add(passwordField);
        jPanel.add(new JLabel(""));

        JPanel jLabelButton = new JPanel();
        jLabelButton.setLayout(new GridLayout(0,2));
        jLabelButton.add(new JLabel(""));
        jLabelButton.add(signupButton);

        jPanel.add(jLabelButton);

        jPanel.setBorder(new EmptyBorder(10,10,10,10));

        getContentPane().add(jPanel, BorderLayout.CENTER);
        setVisible(true);

        getRootPane().setDefaultButton(signupButton);

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

                if (checkUserNameValid(user) && user.length() > 0 && pass.length() > 0)
                {
                    signUp(user,pass);
                }
            }
        });
    }

    private boolean checkUserNameValid(String userName)
    {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(userName);
        if(m.find())
        {
            JOptionPane.showMessageDialog(this,"Username must not contain special character!");
            return false;
        }
        return true;
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
