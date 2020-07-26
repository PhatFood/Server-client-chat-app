package com.company;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Login extends JFrame {
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton signupButton = new JButton("Sign Up");
    private JLabel userNameLabel = new JLabel("User name: ");
    private JLabel passwordLabel = new JLabel("Password: ");
    private volatile boolean isConnected = false;

    private ClientHandle client;

    public Login() throws IOException {
        super("Login");
        setSize(300, 200);
        setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(6,0));
        jPanel.add(userNameLabel);
        jPanel.add(userTextField);
        jPanel.add(passwordLabel);
        jPanel.add(passwordField);
        jPanel.add(new JLabel(""));

        JPanel jPanelButton = new JPanel();
        jPanelButton.setLayout(new GridLayout(0,2));
        jPanelButton.add(loginButton);
        jPanelButton.add(signupButton);

        jPanel.add(jPanelButton);

        jPanel.setBorder(new EmptyBorder(10,10,10,10));

        getContentPane().add(jPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);

        Thread thread = new Thread(this::ConnectServer);
        thread.start();


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isConnected)
                {
                    try {
                        sendLoginRequest();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else {
                    showErrorConnect();
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isConnected) {
                    try {
                        sendSignupRequest();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else {
                    showErrorConnect();
                }
            }
        });

        getRootPane().setDefaultButton(loginButton);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                client.terminate();
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

    private void showErrorConnect() {
        JOptionPane.showMessageDialog(this, "Cannot connect to server!");
    }

    private void ConnectServer(){
        while(!isConnected) {
            try {
                this.client = new ClientHandle("localhost", 8888);
                isConnected = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        super.addKeyListener(l);
    }

    private void sendSignupRequest() throws IOException {
        Signup signup = new Signup(client, this);
        signup.setVisible(true);

        this.setVisible(false);
    }

    private void sendLoginRequest() throws IOException {
        String user = userTextField.getText().toLowerCase();
        String pass = passwordField.getText();

        if (client.login(user, pass)) {
            ClientMainGUI clientMainGUI = new ClientMainGUI(client);
            client.handleSocket();

            JFrame jFrame = new JFrame("You logged with name: " + user);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setSize(480, 500);
            jFrame.addWindowListener(clientMainGUI);
            jFrame.getContentPane().add(clientMainGUI, BorderLayout.CENTER);
            jFrame.setLocationRelativeTo(null);
            jFrame.setLocation(this.getLocation());
            jFrame.setVisible(true);

            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please check your user name or password");
        }
    }

    public static void main(String[] args) throws IOException {
        Login login = new Login();
        login.setVisible(true);
    }
}
