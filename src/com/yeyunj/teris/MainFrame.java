package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private final static int width = 600;
    private final static int height = 800;
    //    private static com.yeyunj.teris.MainFrame m_instance;
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
    private final int default_speed = 500;

    //一秒调用一次
    private final int constant_speed_timer_interval = 400;

    //菜单栏部分
    private JMenuBar jMenuBar;
    private JMenu game_jmenu;
    private JMenuItem new_game_jmenuitem;
    private JMenuItem pause_game_jmenuitem;
    private JMenu setting_jmenu;
    private JMenuItem speed_jmenuitem;
    private JMenu about_jmenu;
    private JMenuItem about_author_jmenuitem;
    private JMenuItem about_project_jmenuitem;
    private JMenuItem about_playing_method_jmenuitem;

    //布局
    private GridBagLayout gridBagLayout;

    //方块面板
    private BlockPanel blockPanel;
    //分数面板
    private ScorePanel scorePanel;


    //游戏主计时器，负责每帧的调用逻辑，下落一次为一帧
    private Timer game_tick_timer;
    private Timer constant_speed_timer;

    //游戏速度变量
    private int speed;


    public MainFrame() {
        this.setTitle("俄罗斯方块");
//        setLocationRelativeTo(null);

        //创建菜单栏
        jMenuBar = new JMenuBar();
        this.setJMenuBar(jMenuBar);

        //为菜单栏添加游戏选项
        game_jmenu = new JMenu("游戏");
        jMenuBar.add(game_jmenu);
        //为游戏选项添加新游戏功能
        new_game_jmenuitem = new JMenuItem("新游戏");
        new_game_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speed = default_speed;
                MainFrame.this.blockPanel.Reset();
                game_tick_timer.setDelay(speed);
            }
        });
        game_jmenu.add(new_game_jmenuitem);
        //为游戏选项添加暂停功能
        pause_game_jmenuitem = new JMenuItem("暂停");
        pause_game_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllTimer();
                JOptionPane.showMessageDialog(MainFrame.this, "游戏已暂停");
                startAllTimer();
            }
        });
        game_jmenu.add(pause_game_jmenuitem);

        //为菜单栏添加设置选项
        setting_jmenu = new JMenu("设置");
        jMenuBar.add(setting_jmenu);
        //设置选项添加调速功能
        speed_jmenuitem = new JMenuItem("速度");
        speed_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllTimer();
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
                game_tick_timer.setDelay(speed);
                startAllTimer();
            }
        });
        setting_jmenu.add(speed_jmenuitem);

        //为菜单栏添加设置选项
        about_jmenu = new JMenu("关于");
        jMenuBar.add(about_jmenu);

        //关于选项添加关于作者功能
        about_author_jmenuitem = new JMenuItem("关于作者");
        about_author_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllTimer();
                JOptionPane.showMessageDialog(MainFrame.this,
                        about_author_message,
                        "关于作者",
                        JOptionPane.PLAIN_MESSAGE);
                startAllTimer();
            }
        });
        about_jmenu.add(about_author_jmenuitem);


        //关于选项添加关于项目功能
        about_project_jmenuitem = new JMenuItem("关于本软件");
        about_project_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllTimer();
                JOptionPane.showMessageDialog(MainFrame.this,
                        about_project_message,
                        "关于本软件",
                        JOptionPane.PLAIN_MESSAGE);


                startAllTimer();
            }
        });
        about_jmenu.add(about_project_jmenuitem);

        about_playing_method_jmenuitem=new JMenuItem("关于玩法");
        about_playing_method_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllTimer();
                PlayingMethodDialog playingMethodDialog=new PlayingMethodDialog(MainFrame.this);
                playingMethodDialog.setVisible(true);

                startAllTimer();
            }
        });
        about_jmenu.add(about_playing_method_jmenuitem);

        //设置布局
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


        //添加方块面板
        this.add(blockPanel);
        //添加分数面板
        this.add(scorePanel);


        //设置按键监听
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
                        if (game_tick_timer.getDelay() != speed / 10) {
                            game_tick_timer.setDelay(speed / 10);
                        }
                    }
                }
                MainFrame.this.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //如果松开下降按键则恢复原速度
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    game_tick_timer.setDelay(speed);
                }
            }
        });

        //初始化为默认速度
        speed = default_speed;
        //创建游戏主计时器
        game_tick_timer = new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean fix_failure=false;
                boolean generated_failure;

                //在底下生成方块前检测一下，防止生成完后新方块嵌入移动的方块中
                if(!MainFrame.this.blockPanel.canMoveDown()){
                    //这种情况肯定不会fail因此忽略了返回值
                    MainFrame.this.blockPanel.whenBlocksFixShouldDoAndDetectFailure();
                }
                generated_failure=MainFrame.this.blockPanel.generateBlockAtBottomAndDetectFailure();

                if(!generated_failure){
                    boolean touched_other_block = MainFrame.this.blockPanel.TryMoveDown();

//                    MainFrame.this.repaint();
                    if (touched_other_block) {
                        fix_failure = MainFrame.this.blockPanel.whenBlocksFixShouldDoAndDetectFailure();
                    }
                }

                //先画图再弹输了的窗口，防止最后一帧不显示导致看起来很奇怪
                MainFrame.this.repaint();

                if(fix_failure||generated_failure){
                    stopAllTimer();
                    JOptionPane.showMessageDialog(MainFrame.this, "你输了");
                    MainFrame.this.blockPanel.Reset();
                    speed = default_speed;
                    game_tick_timer.setDelay(speed);
                    startAllTimer();
                }

            }
        });

        constant_speed_timer=new Timer(constant_speed_timer_interval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.blockPanel.incSecondCount();
            }
        });

        //设置窗口的一些参数
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.requestFocus();
        this.setVisible(true);

        //启动计时器
        startAllTimer();
    }

    private void startAllTimer(){
        constant_speed_timer.start();
        game_tick_timer.start();
    }

    private void stopAllTimer(){
        constant_speed_timer.stop();
        game_tick_timer.stop();
    }

//    public static com.yeyunj.teris.MainFrame getInstance(){
//        if(m_instance==null){
//            m_instance=new com.yeyunj.teris.MainFrame();
//        }
//        return m_instance;
//    }


    private static void startUI() {
        MainFrame mainFrame = new MainFrame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startUI();
            }
        });
    }
}
