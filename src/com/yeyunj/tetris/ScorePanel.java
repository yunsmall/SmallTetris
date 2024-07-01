package com.yeyunj.tetris;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private BlockPanel blockPanel;

    private double[] score_title_location={0.2,0.05};

    private double[] score_location={0.2,0.1};

    private double[] max_score_title_location={0.2,0.15};

    private double[] max_score_location={0.2,0.2};

    private double[] next_block_title_location={0.2,0.3};

    private double next_block_location_y=0.5;

    private double[] paused_hint_location={0.2,0.4};

    private int block_x_pixel_offset;
//    private int block_y_offset;

    public ScorePanel(BlockPanel blockPanel) {
        this.blockPanel=blockPanel;
//        this.setBackground(Color.BLUE);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0,0,this.getWidth(),this.getHeight());

        Font size20font=new Font("黑体",Font.BOLD,20);
        Font size30font=new Font("黑体",Font.BOLD,30);

        g.setColor(Color.ORANGE);
        g.setFont(size20font);
        g.drawString("当前得分：", (int) (score_title_location[0]*this.getWidth()), (int) (score_title_location[1]*this.getHeight()));
        g.setFont(size30font);
        g.drawString(String.valueOf(this.blockPanel.getScore()), (int) (score_location[0]*this.getWidth()), (int) (score_location[1]*this.getHeight()));

        g.setFont(size20font);
        g.drawString("最高得分：", (int) (max_score_title_location[0]*this.getWidth()), (int) (max_score_title_location[1]*this.getHeight()));
        g.setFont(size30font);
        g.drawString(String.valueOf(this.blockPanel.getCurrentUserMaxScore()), (int) (max_score_location[0]*this.getWidth()), (int) (max_score_location[1]*this.getHeight()));

        g.setFont(size20font);
        g.drawString("下一个方块：", (int) (next_block_title_location[0]*this.getWidth()), (int) (next_block_title_location[1]*this.getHeight()));

        if(blockPanel.isPaused()){
            g.setColor(Color.RED);
            g.setFont(size20font);
            g.drawString("游戏已暂停", (int) (paused_hint_location[0]*this.getWidth()), (int) (paused_hint_location[1]*this.getHeight()));
        }

        int next_blocks_max_width=this.blockPanel.getNext_block().getMaxWidth();
        int next_blocks_max_height=this.blockPanel.getNext_block().getMaxHeight();

        int[] left_up=this.blockPanel.getNext_block().getData().getLeftUp();

        block_x_pixel_offset =(this.getWidth()-this.blockPanel.getBlock_width()*next_blocks_max_width)/2;
        if(block_x_pixel_offset <0){
            block_x_pixel_offset =0;
        }

        g.setColor(Color.WHITE);
        g.fillRect(block_x_pixel_offset -10,(int)(this.getHeight()*next_block_location_y)-10,10*2+this.blockPanel.getBlock_width()*next_blocks_max_width,10*2+this.blockPanel.getBlock_width()*next_blocks_max_height);

        //画粗线
        g.setColor(Color.ORANGE);
        //竖线
        for(int i=0;i<=next_blocks_max_width;i++){
            for(int j=-2;j<3;j++){
                if(j!=0){
                    g.drawLine(block_x_pixel_offset +i*this.blockPanel.getBlock_width()+j,(int)(this.getHeight()*next_block_location_y)-2, block_x_pixel_offset +i*this.blockPanel.getBlock_width()+j,(int)(this.getHeight()*next_block_location_y+this.blockPanel.next_block.getMaxHeight()*this.blockPanel.getBlock_width())+2);
                }
            }
        }
        //横线
        for(int i=0;i<=next_blocks_max_height;i++){
            for(int j=-2;j<3;j++){
                if(j!=0){
                    g.drawLine(block_x_pixel_offset -2,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width())+j, block_x_pixel_offset +next_blocks_max_width*this.blockPanel.getBlock_width()+2,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width())+j);
                }
            }
        }


        g.setColor(Color.BLACK);
        //竖线
        for(int i=0;i<=next_blocks_max_width;i++){
            g.drawLine(block_x_pixel_offset +i*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y), block_x_pixel_offset +i*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y+this.blockPanel.next_block.getMaxHeight()*this.blockPanel.getBlock_width()));
        }
        //横线
        for(int i=0;i<=next_blocks_max_height;i++){
            g.drawLine(block_x_pixel_offset,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width()), block_x_pixel_offset +next_blocks_max_width*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width()));
        }

        g.setColor(this.blockPanel.getNext_block().getColor());

        for(int i=0;i<=this.blockPanel.getNext_block().getBlockLen();i++){
            for(int[] point:this.blockPanel.getNext_block().getData().getDatas()){
                g.fillRect(block_x_pixel_offset +(point[0]-left_up[0])*this.blockPanel.getBlock_width()+3,(int)(this.getHeight()*next_block_location_y+(point[1]-left_up[1])*this.blockPanel.getBlock_width())+3,this.blockPanel.getBlock_width()-5,this.blockPanel.getBlock_width()-5);
            }
        }

//        for(int i=0;i<next_blocks_max_width;i++){
//            for(int j=0;j<next_blocks_max_height;j++){
//                if(this.blockPanel.getNext_block().getBlock()[j][i]!=0){
//                    g.fillRect(block_x_offset+i*this.blockPanel.getBlock_width()+3,(int)(this.getHeight()*next_block_location_y+j*this.blockPanel.getBlock_width())+3,this.blockPanel.getBlock_width()-5,this.blockPanel.getBlock_width()-5);
////                    g.fillRect(x_offset+i*block_width+2,y_offset+j*block_width+2,block_width-4,block_width-4);
//                }
//            }
//        }

//        g.drawString("6666",100,200);
    }
}
