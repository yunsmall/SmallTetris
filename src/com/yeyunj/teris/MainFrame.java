package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private final static int width = 600;
    private final static int height = 800;
    private final static String about_author_message = """
            组名；OutOfMemory
            组长：叶蕴杰
            组员：王家骏、陈逸远、陈杰凯、李鑫
                        
            叶蕴杰：人员调配协调、项目开发
            王家骏：项目需求分析及项目进度管理
            陈逸远：确立功能需求及项目开发
            陈杰凯：产品定义及项目测试
            李鑫：计划表、任务书、功能说明、实验报告的撰写""";
    private final static String about_project_message = """
            项目说明""";
    private final int default_speed = 800;

    private JMenuBar jMenuBar;
    private JMenu game_jmenu;
    private JMenuItem new_game_jmenuitem;
    private JMenuItem pause_game_jmenuitem;
    private JMenu setting_jmenu;
    private JMenuItem speed_jmenuitem;
    private JMenu about_jmenu;
    private JMenuItem about_author_jmenuitem;
    private JMenuItem about_project_jmenuitem;

    private GridBagLayout gridBagLayout;
    private BlockPanel blockPanel;
    private ScorePanel scorePanel;

    private Timer game_timer;
    private int speed;

    public MainFrame() {
        this.setTitle("俄罗斯方块");

        jMenuBar = new JMenuBar();
        this.setJMenuBar(jMenuBar);

        game_jmenu = new JMenu("游戏");
        jMenuBar.add(game_jmenu);
        new_game_jmenuitem = new JMenuItem("新游戏");
        new_game_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speed = default_speed;
                MainFrame.this.blockPanel.Reset();
                game_timer.setDelay(speed);
            }
        });
        game_jmenu.add(new_game_jmenuitem);
        pause_game_jmenuitem = new JMenuItem("暂停");
        pause_game_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game_timer.stop();
                JOptionPane.showMessageDialog(MainFrame.this, "游戏已暂停");
                game_timer.start();
            }
        });
        game_jmenu.add(pause_game_jmenuitem);

        setting_jmenu = new JMenu("设置");
        jMenuBar.add(setting_jmenu);
        speed_jmenuitem = new JMenuItem("速度");
        speed_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game_timer.stop();
                while (true) {
                    try {
                        String ans = JOptionPane.showInputDialog(MainFrame.this, "请输入你想每多少毫秒下降一次", String.valueOf(speed));
                        if (ans != null) {
                            speed = Integer.parseInt(ans);
                        }
                        break;
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(MainFrame.this, "输入不合法，请重新输入");
                    }
                }
                game_timer.setDelay(speed);
                game_timer.start();
            }
        });
        setting_jmenu.add(speed_jmenuitem);

        about_jmenu = new JMenu("关于");
        jMenuBar.add(about_jmenu);
        about_author_jmenuitem = new JMenuItem("关于作者");
        about_author_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game_timer.stop();
                JOptionPane.showMessageDialog(MainFrame.this, about_author_message, "关于作者", JOptionPane.PLAIN_MESSAGE);
                game_timer.start();
            }
        });
        about_jmenu.add(about_author_jmenuitem);

        about_project_jmenuitem = new JMenuItem("关于本软件");
        about_project_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game_timer.stop();
                JOptionPane.showMessageDialog(MainFrame.this, about_project_message, "关于本软件", JOptionPane.PLAIN_MESSAGE);
                game_timer.start();
            }
        });
        about_jmenu.add(about_project_jmenuitem);

        gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 5;
        gridBagConstraints.weighty = 1;
        blockPanel = new BlockPanel();
        gridBagLayout.addLayoutComponent(blockPanel, gridBagConstraints);

        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 2;
        gridBagConstraints.weighty = 1;
        scorePanel = new ScorePanel(this.blockPanel);
        gridBagLayout.addLayoutComponent(scorePanel, gridBagConstraints);

        this.add(blockPanel);
        this.add(scorePanel);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT -> {
                        blockPanel.NextStep(BlockPanel.BlockAction.MoveRight);
                    }
                    case KeyEvent.VK_LEFT -> {
                        blockPanel.NextStep(BlockPanel.BlockAction.MoveLeft);
                    }
                    case KeyEvent.VK_UP -> {
                        blockPanel.NextStep(BlockPanel.BlockAction.Rot);
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (game_timer.getDelay() != speed / 10) {
                            game_timer.setDelay(speed / 10);
                        }
                    }
                }
                MainFrame.this.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    game_timer.setDelay(speed);
                }
            }
        });

        speed = default_speed;
        game_timer = new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean is_end = MainFrame.this.blockPanel.TryMoveDown();
                MainFrame.this.repaint();

                if (is_end) {
                    boolean is_failed = MainFrame.this.blockPanel.FixCurrentBlockAndDetectFailue();
                    if (!is_failed) {
                        MainFrame.this.blockPanel.DetectAndDeleteLine();
                        MainFrame.this.blockPanel.ChangeBlock();
                    } else {
                        game_timer.stop();
                        JOptionPane.showMessageDialog(MainFrame.this, "你输了");
                        MainFrame.this.blockPanel.Reset();
                        speed = default_speed;
                        game_timer.setDelay(speed);
                        game_timer.start();
                    }
                }
            }
        });

        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.requestFocus();
        this.setVisible(true);

        game_timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startUI();
            }
        });
    }

    private static void startUI() {
        MainFrame mainFrame = new MainFrame();
    }
}
