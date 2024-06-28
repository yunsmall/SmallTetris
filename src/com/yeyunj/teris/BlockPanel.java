package com.yeyunj.teris;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BlockPanel extends JPanel {
    private final int x_blocks = 10;
    private final int y_blocks = 20;
    private int x_offset = 0;
    private int y_offset = 0;

    private int[][] map = new int[y_blocks][x_blocks];


    private int block_width;

    Blocks current_block;

    private int current_x = x_blocks / 2;
    //    private int current_y=-current_block.getBlockLen();
    private int current_y = 0;

    private int score = 0;
    Blocks next_block;

    public BlockPanel() {
//        this.setBackground(Color.RED);
//        for(int i=0;i<y_blocks;i++){
//            map[i][3]=3;
//        }
//        for(int i=0;i<x_blocks;i++){
//            map[4][i]=5;
//        }
        this.ResetBlock();
//        for(int i=10;i<this.y_blocks;i++){
//            for(int j=1;j<x_blocks;j++){
//                this.map[i][j]=1;
//            }
//        }
//        current_block.setColor(Color.RED);
    }

    public enum BlockAction {
        MoveRight,
        MoveLeft,
        Rot
    }

    /**
     * 第一个白色无效，map中代表0，即空
     */
    private static Color[] color_map = {
            Color.WHITE,
            Color.BLACK,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.BLUE,
            Color.PINK,
            Color.GRAY
    };

    public static Color SampleColor() {
        return color_map[new Random().nextInt(1, color_map.length - 1)];
    }


    @Override
    public void paint(Graphics g) {
        //横向有空位
        if (this.getHeight() / y_blocks < this.getWidth() / x_blocks) {
            block_width = this.getHeight() / y_blocks;
            x_offset = (this.getWidth() - x_blocks * block_width) / 2;
        }
        //纵向有空位
        else {
            block_width = this.getWidth() / x_blocks;
            y_offset = (this.getHeight() - y_blocks * block_width) / 2;
        }


//        g.setColor(Color.WHITE);
//        g.fillRect(0,0,this.getWidth(),this.getHeight());

        //画粗线条
        g.setColor(Color.ORANGE);
        //竖线
        for (int i = 0; i <= x_blocks; i++) {
            int now_x = x_offset + block_width * i;
            for (int j = -2; j < 3; j++) {
                if (j != 0) {
                    g.drawLine(now_x + j, y_offset, now_x + j, y_offset + y_blocks * block_width);
                }

            }
        }
        //横线
        for (int i = 0; i <= y_blocks; i++) {
            int now_y = y_offset + block_width * i;
            for (int j = -2; j < 3; j++) {
                if (j != 0) {
                    g.drawLine(x_offset, now_y + j, x_offset + x_blocks * block_width, now_y + j);
                }
            }
        }

        //画细线条
        g.setColor(Color.BLACK);
        //竖线
        for (int i = 0; i <= x_blocks; i++) {
            int now_x = x_offset + block_width * i;
            g.drawLine(now_x, y_offset, now_x, y_offset + y_blocks * block_width);

        }
        //横线
        for (int i = 0; i <= y_blocks; i++) {
            int now_y = y_offset + block_width * i;
            g.drawLine(x_offset, now_y, x_offset + x_blocks * block_width, now_y);

        }

        //画存在的方块
        for (int i = 0; i < x_blocks; i++) {
            for (int j = 0; j < y_blocks; j++) {
                if (map[j][i] != -1) {
                    g.setColor(color_map[map[j][i]]);
                    DrawXYBlock(g, i, j);
//                    g.fillRect(x_offset+i*block_width+2,y_offset+j*block_width+2,block_width-4,block_width-4);
                }
            }
        }
        //画当前下落的方块
        g.setColor(current_block.getColor());
        for (int i = 0; i < current_block.getBlockLen(); i++) {
            for (int j = 0; j < current_block.getBlockLen(); j++) {
                if (current_block.getBlock()[j][i] == 1) {
                    if (i + current_x >= 0 && j + current_y >= 0) {
                        DrawXYBlock(g, i + current_x, j + current_y);
                    }

//                    g.fillRect(x_offset+(i+current_x)*block_width+2,y_offset+(j+current_y)*block_width+2,block_width-4,block_width-4);
                }

            }
        }


    }

    /**
     * 到底了返回true，否则返回false
     *
     * @return 是否到底
     */
    public boolean MoveDown() {
        boolean end = false;

        //外层遍历列
        outfor:
        for (int i = 0; i < this.current_block.getBlockLen(); i++) {
            //内层遍历行
            for (int j = this.current_block.getBlockLen() - 1; j >= 0; j--) {
                //如果遍历到了方块中有方块的格子
                if (this.current_block.getBlock()[j][i] != 0) {
                    //如果下一个是底或其他方块
                    //如果当前遍历到纵坐标在屏幕外，会产生异常，直接捕获
                    try {
                        if (current_y + j + 1 >= y_blocks || map[current_y + j + 1][current_x + i] != 0) {
                            end = true;
                            break outfor;
                        }
                    } catch (IndexOutOfBoundsException ignored) {

                    }

                    break;
                }
            }
        }

        if (!end) {
            current_y++;
        }
        return end;
    }

    public boolean FixAndDetectFailue() {
        int index = getColorIndex(this.current_block.getColor());
        if (index == -1) {
            index = 0;
        }
        boolean is_failed = false;
        try {
            for (int i = 0; i < current_block.getBlockLen(); i++) {
                for (int j = 0; j < current_block.getBlockLen(); j++) {
                    if (this.current_block.getBlock()[j][i] != 0) {
                        this.map[current_y + j][current_x + i] = index;
                    }

                }
            }
        } catch (IndexOutOfBoundsException e) {
            is_failed = true;
        }
        return is_failed;

    }

    public void DetectAndDeleteLine() {
        double start_rate = 10;//只消掉一行加的分
        double rate = 2;//每多消掉一行增加的倍数
        int count_full_line = 0;
        for (int i = 0; i < this.y_blocks; i++) {
            boolean is_full = true;
            for (int j = 0; j < this.x_blocks; j++) {
                if (this.map[i][j] == 0) {
                    is_full = false;
                    break;
                }
            }
            //如果这行满了
            if (is_full) {
                count_full_line++;
                //遍历每行
                for (int ti = i - 1; ti >= 0; ti--) {
                    //遍历每列
                    for (int tj = 0; tj < x_blocks; tj++) {
                        //上行移下来
                        this.map[ti + 1][tj] = this.map[ti][tj];
                        //上一行清空
                        this.map[ti][tj] = 0;
                    }
                }
            }
            //如果消掉了行
            if (count_full_line != 0) {
                //加分
                this.score += start_rate * Math.pow(rate, count_full_line - 1);
            }

        }

    }

    public void ResetBlock() {
        this.next_block = Blocks.GetNextBlock();
        this.next_block.setColor(BlockPanel.SampleColor());
        this.ChangeBlock();
    }

    public void ChangeBlock() {
        this.current_block = this.next_block;
        this.next_block = Blocks.GetNextBlock();
        this.next_block.setColor(BlockPanel.SampleColor());
        this.current_x = getDefaultX();
        this.current_y = getDefaultY();
    }

    public void Reset() {
        this.score = 0;
        for (int i = 0; i < y_blocks; i++) {
            for (int j = 0; j < x_blocks; j++) {
                this.map[i][j] = 0;
            }
        }
        this.ResetBlock();
    }

    /**
     * 返回颜色在数组中的位置，不在数组中返回-1
     *
     * @param color 查找的颜色
     * @return 位置
     */
    private int getColorIndex(Color color) {
        for (int i = 0; i < color_map.length; i++) {
            if (color_map[i].equals(color)) {
                return i;
            }
        }
        return -1;
    }

    public int getBlock_width() {
        return block_width;
    }


    private void DrawXYBlock(Graphics g, int x, int y) {
        g.fillRect(x_offset + x * block_width + 3, y_offset + y * block_width + 3, block_width - 5, block_width - 5);
    }

    public Blocks getNext_block() {
        return next_block;
    }

    public void SetXY(int x, int y) {
        this.current_x = x;
        this.current_y = y;
    }

    /**
     * 返回默认初始化的X
     *
     * @return 初始化的X
     */
    public int getDefaultX() {
        return x_blocks / 2;
    }

    /**
     * 返回默认初始化的Y
     *
     * @return 初始化的Y
     */
    public int getDefaultY() {
        return -this.current_block.getMaxHeight();
    }

    public int getScore() {
        return this.score;
    }

    public void NextStep(BlockAction action) {
        switch (action) {
            case MoveLeft -> {
                if (this.current_x > 0) {


                    boolean can_move = true;
                    //遍历第一列的每一行
                    outfor:
                    for (int j = 0; j < this.current_block.getMaxHeight(); j++) {
                        //如果当前遍历到坐标在屏幕外，会产生异常，直接捕获
                        try {
                            for (int i = 0; i < this.current_block.getBlockLen(); i++) {
                                if (map[current_y + j][current_x + i - 1] != 0 && this.current_block.getBlock()[j][i] != 0) {
                                    can_move = false;
                                    break outfor;
                                }
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                    }
                    if (can_move) {
                        this.current_x--;
                    }

                }
            }
            case MoveRight -> {
                if (this.current_x < this.x_blocks - current_block.getMaxWidth()) {

                    boolean can_move = true;
                    //遍历最后一列的每一行
                    outfor:
                    for (int j = 0; j < this.current_block.getMaxHeight(); j++) {
                        //如果当前遍历到纵坐标在屏幕外，会产生异常，直接捕获
                        try {
                            for (int i = 0; i < this.current_block.getBlockLen(); i++) {
                                if (map[current_y + j][current_x + i] != 0 && this.current_block.getBlock()[j][i - 1] != 0) {
                                    can_move = false;
                                    break outfor;
                                }
                            }

                        } catch (IndexOutOfBoundsException ignored) {

                        }

                    }
                    if (can_move) {
                        this.current_x++;
                    }

                }
            }
            case Rot -> {
                this.current_block.Rot(map, current_x, current_y);
            }
        }
    }


}
