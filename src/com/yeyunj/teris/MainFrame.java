package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame {

    private final static int width=600;
    private final static int height=800;
//    private static com.yeyunj.teris.MainFrame m_instance;


    private JMenuBar jMenuBar;
    private JMenu game_jmenu;
    private JMenuItem new_game_jmenuitem;
    private JMenu setting_jmenu;
    private JMenuItem speed_jmenuitem;
    private JMenu about_jmenu;

    private GridBagLayout gridBagLayout;


    private BlockPanel blockPanel;
    private ScorePanel scorePanel;

    private Timer timer;

    private int speed;


    public MainFrame(){
        this.setTitle("俄罗斯方块");


        jMenuBar=new JMenuBar();
        this.setJMenuBar(jMenuBar);

        game_jmenu=new JMenu("游戏");
        jMenuBar.add(game_jmenu);
        new_game_jmenuitem=new JMenuItem("新游戏");
        new_game_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.blockPanel.Reset();
            }
        });
        game_jmenu.add(new_game_jmenuitem);



        setting_jmenu=new JMenu("设置");
        jMenuBar.add(setting_jmenu);
        speed_jmenuitem=new JMenuItem("速度");
        speed_jmenuitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                while(true){
                    try{
                        String ans=JOptionPane.showInputDialog(MainFrame.this,"请输入你想每多少毫秒下降一次","修改速度");
                        if(ans!=null){
                            speed=Integer.parseInt(ans);
                        }
                        break;
                    }
                    catch (NumberFormatException exception){
                        JOptionPane.showMessageDialog(MainFrame.this,"输入不合法，请重新输入");
//                        continue;
                    }
                }
                timer.setDelay(speed);
                timer.start();


            }
        });
        setting_jmenu.add(speed_jmenuitem);

        about_jmenu=new JMenu("关于");
        jMenuBar.add(about_jmenu);

        gridBagLayout=new GridBagLayout();


        this.setLayout(gridBagLayout);



        GridBagConstraints gridBagConstraints=new GridBagConstraints();
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=5;
        gridBagConstraints.weighty=1;
        blockPanel=new BlockPanel();
        gridBagLayout.addLayoutComponent(blockPanel,gridBagConstraints);

        gridBagConstraints.gridx=7;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=2;
        gridBagConstraints.weighty=1;
        scorePanel=new ScorePanel(this.blockPanel);
        gridBagLayout.addLayoutComponent(scorePanel,gridBagConstraints);

        this.add(blockPanel);
        this.add(scorePanel);

//        this.setFocusable(true);
//        this.requestFocus();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()){
                    case KeyEvent.VK_RIGHT->{
                        blockPanel.NextStep(BlockPanel.BlockAction.MoveRight);
                    }
                    case KeyEvent.VK_LEFT->{
                        blockPanel.NextStep(BlockPanel.BlockAction.MoveLeft);
                    }
                    case KeyEvent.VK_UP->{
                        blockPanel.NextStep(BlockPanel.BlockAction.Rot);
                    }
                    case KeyEvent.VK_DOWN->{
                        if(timer.getDelay()!=speed/10){
                            timer.setDelay(speed/10);
//                            timer.
                        }


                    }

                }
                MainFrame.this.repaint();
            }


            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    timer.setDelay(speed);
                }
            }
        });

        speed=800;
        timer=new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean is_end=MainFrame.this.blockPanel.MoveDown();

                MainFrame.this.repaint();

                if(is_end){
                    boolean is_failed=MainFrame.this.blockPanel.FixAndDetectFailue();
                    if(!is_failed){
                        MainFrame.this.blockPanel.DetectAndDeleteLine();
                        MainFrame.this.blockPanel.ChangeBlock();
//                        MainFrame.this.blockPanel.SetXY(MainFrame.this.blockPanel.getDefaultX(),MainFrame.this.blockPanel.getDefaultY());
//                        MainFrame.this.scorePanel.repaint();
                    }
                    else{
                        JOptionPane.showMessageDialog(MainFrame.this,"你输了");
                        MainFrame.this.blockPanel.Reset();
                    }


                }
            }
        });


        this.setSize(width,height);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.requestFocus();
        this.setVisible(true);

        timer.start();

//        blockPanel.requestFocus();


    }

//    public static com.yeyunj.teris.MainFrame getInstance(){
//        if(m_instance==null){
//            m_instance=new com.yeyunj.teris.MainFrame();
//        }
//        return m_instance;
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startUI();
            }
        });
    }

    private static void startUI(){
        MainFrame mainFrame=new MainFrame();
//        mainFrame.setVisible(true);
    }
}