package com.yeyunj.teris;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame {
    private JTextField accountText;
    private JPasswordField passwordField;
    private JLabel accountLabel;
    private JLabel passwordLabel;
    JPanel root;
    private JButton confirmButton;
    private JButton registerButton;

    public LoginFrame() {
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryLogin(accountLabel.getText(), passwordLabel.getText());
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame)SwingUtilities.getRoot(root);
                frame.dispose();
            }
        });
    }

    private void tryLogin(String account, String password) {

    }

    private void tryRegister(String account, String password) {

    }
}
