package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayingMethodDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JPanel colorPanel;

    public PlayingMethodDialog(Window owner) {
        super(owner);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(500,400);
        setLocationRelativeTo(null);

        colorPanel.setBackground(BlockPanel.rainbow_color);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        // 在此处添加您的代码
        dispose();
    }
}
