package com.prismflux.schrottimon.auth;

import com.prismflux.schrottimon.AuthenticationClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPrompt extends JFrame implements ActionListener {
    private JTextField username;
    private JPasswordField password;
    private AuthenticationClient auth = new AuthenticationClient();

    public LoginPrompt() {
        JPanel p1 = new JPanel();
        p1.add(new JLabel("Username"));
        username = new JTextField();
        username.setPreferredSize(new Dimension(150, 24));
        p1.add(username);

        JPanel p2 = new JPanel();
        p2.add(new JLabel("Password"));
        password = new JPasswordField();
        password.setPreferredSize(new Dimension(150, 24));
        p2.add(password);

        add(p1);
        add(p2);

        JButton jb = new JButton("Login");
        jb.addActionListener(this);
        add(jb);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //try {
        try {
            auth.login(username.getText(), password.getText());
        } catch (Exception exc) {
            exc.printStackTrace();
            JOptionPane.showMessageDialog(this, "No Connection to server");
        }
    }
}
