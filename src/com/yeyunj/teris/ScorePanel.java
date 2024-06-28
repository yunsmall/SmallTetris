package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private BlockPanel blockPanel;

    private double[] score_title_location={0.2,0.05};

    private double[] score_location={0.2,0.1};

    private double[] next_block_title_location={0.2,0.2};

    private double next_block_location_y=0.3;

    private int block_x_offset;
//    private int block_y_offset;

    public ScorePanel(BlockPanel blockPanel) {
        this.blockPanel=blockPanel;
//        this.setBackground(Color.BLUE);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0,0,this.getWidth(),this.getHeight());

        g.setColor(Color.ORANGE);
        g.setFont(new Font("黑体",Font.BOLD,20));
        g.drawString("当前得分：", (int) (score_title_location[0]*this.getWidth()), (int) (score_title_location[1]*this.getHeight()));
        g.setFont(new Font("黑体",Font.BOLD,30));
        g.drawString(String.valueOf(this.blockPanel.getScore()), (int) (score_location[0]*this.getWidth()), (int) (score_location[1]*this.getHeight()));

        g.setFont(new Font("黑体",Font.BOLD,20));
        g.drawString("下一个方块：", (int) (next_block_title_location[0]*this.getWidth()), (int) (next_block_title_location[1]*this.getHeight()));

        block_x_offset=(this.getWidth()-this.blockPanel.getBlock_width()*this.blockPanel.getNext_block().getMaxWidth())/2;
        if(block_x_offset<0){
            block_x_offset=0;
        }

        g.setColor(Color.WHITE);
        g.fillRect(block_x_offset-10,(int)(this.getHeight()*next_block_location_y)-10,10*2+this.blockPanel.getBlock_width()*this.blockPanel.getNext_block().getMaxWidth(),10*2+this.blockPanel.getBlock_width()*this.blockPanel.getNext_block().getMaxHeight());

        //画粗线
        g.setColor(Color.ORANGE);
        //竖线
        for(int i=0;i<=this.blockPanel.getNext_block().getMaxWidth();i++){
            for(int j=-2;j<3;j++){
                if(j!=0){
                    g.drawLine(block_x_offset+i*this.blockPanel.getBlock_width()+j,(int)(this.getHeight()*next_block_location_y)-2,block_x_offset+i*this.blockPanel.getBlock_width()+j,(int)(this.getHeight()*next_block_location_y+this.blockPanel.next_block.getMaxHeight()*this.blockPanel.getBlock_width())+2);
                }
            }
        }
        //横线
        for(int i=0;i<=this.blockPanel.getNext_block().getMaxHeight();i++){
            for(int j=-2;j<3;j++){
                if(j!=0){
                    g.drawLine(block_x_offset-2,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width())+j,block_x_offset+this.blockPanel.getNext_block().getMaxWidth()*this.blockPanel.getBlock_width()+2,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width())+j);
                }
            }
        }


        g.setColor(Color.BLACK);
        //竖线
        for(int i=0;i<=this.blockPanel.getNext_block().getMaxWidth();i++){
            g.drawLine(block_x_offset+i*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y),block_x_offset+i*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y+this.blockPanel.next_block.getMaxHeight()*this.blockPanel.getBlock_width()));
        }
        //横线
        for(int i=0;i<=this.blockPanel.getNext_block().getMaxHeight();i++){
            g.drawLine(block_x_offset,(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width()),block_x_offset+this.blockPanel.getNext_block().getMaxWidth()*this.blockPanel.getBlock_width(),(int)(this.getHeight()*next_block_location_y+i*this.blockPanel.getBlock_width()));
        }

        g.setColor(this.blockPanel.getNext_block().getColor());
        for(int i=0;i<this.blockPanel.getNext_block().getMaxWidth();i++){
            for(int j=0;j<this.blockPanel.getNext_block().getMaxHeight();j++){
                if(this.blockPanel.getNext_block().getBlock()[j][i]!=0){
                    g.fillRect(block_x_offset+i*this.blockPanel.getBlock_width()+3,(int)(this.getHeight()*next_block_location_y+j*this.blockPanel.getBlock_width())+3,this.blockPanel.getBlock_width()-5,this.blockPanel.getBlock_width()-5);
//                    g.fillRect(x_offset+i*block_width+2,y_offset+j*block_width+2,block_width-4,block_width-4);
                }
            }
        }

//        g.drawString("6666",100,200);
    }
}
