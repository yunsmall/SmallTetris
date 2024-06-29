package com.yeyunj.teris;

import javax.swing.*;

public class Teris {
    MainFrame mainFrame;
    JFrame loginFrame;

    private Teris(){
        loginFrame=new JFrame("登录");
        loginFrame.setContentPane(new LoginFrame().root);
        loginFrame.setSize(400,300);
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Teris();
    }
}
