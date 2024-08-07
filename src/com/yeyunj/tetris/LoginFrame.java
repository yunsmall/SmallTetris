package com.yeyunj.tetris;

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

    private Tetris from_tetris;

    private LoginFrame(Tetris tetris) {
        from_tetris = tetris;

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryLogin(accountText.getText(), passwordField.getText());
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryRegister(accountText.getText(), passwordField.getText());
            }
        });

        accountText.setText("abcdef");
        passwordField.setText("123456");
    }

    private void tryLogin(String account, String password) {
        LoginManager.ErrorCode ec=new LoginManager.ErrorCode();
        from_tetris.getLoginManager().login(account,password,ec);
        if(ec.getCode()!=LoginManager.ErrorCode.OK){
            JOptionPane.showMessageDialog(root,ec.getMessage());
            return;
        }

        int uid= from_tetris.getLoginManager().getUID(account,ec);
        if(ec.getCode()!=LoginManager.ErrorCode.OK){
            JOptionPane.showMessageDialog(root,ec.getMessage());
            return;
        }

        int max_score= from_tetris.getLoginManager().getHighScore(uid,ec);
        if(ec.getCode()!=LoginManager.ErrorCode.OK){
            JOptionPane.showMessageDialog(root,ec.getMessage());
            return;
        }

        //关闭登录窗口
        SwingUtilities.getWindowAncestor(root).dispose();

        //更新玩家数据
        UserData userData=new UserData(uid,account,max_score);
        from_tetris.userData=userData;
        //创建游戏窗口
        MainFrame mainFrame=new MainFrame(from_tetris);
        //显示游戏窗口
        mainFrame.setVisible(true);
        //启动计时器
        mainFrame.startAllTimer();

    }

    private void tryRegister(String account, String password) {
        LoginManager.ErrorCode ec=new LoginManager.ErrorCode();
        boolean register_ret= from_tetris.getLoginManager().register(account,password,ec);
        if(ec.getCode()!=LoginManager.ErrorCode.OK){
            JOptionPane.showMessageDialog(root,ec.getMessage());
        }
        if(register_ret){
            JOptionPane.showMessageDialog(root,"注册成功");
        }
    }

    public static JFrame genLoginJFrame(Tetris tetris) {
        JFrame loginFrame=new JFrame("登录");
        loginFrame.setContentPane(new LoginFrame(tetris).root);
        loginFrame.setSize(400,300);
        loginFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        return loginFrame;
    }
}
