package com.yeyunj.teris;

import java.awt.*;
import java.util.Random;

public class Blocks {
    private Color color=Color.YELLOW;

//    private int x=2;
//    private int y=0;

//    private boolean need_trans=false;
    private int[][] myblock;

    public int[][] getBlock() {
        return myblock;
    }

    private static final Random random=new Random();
    public static int[][][] blocks={
            {
                    {0,1,0},
                    {1,1,1},
                    {0,0,0}
            },
            {
                    {1,1,1,1},
                    {0,0,0,0},
                    {0,0,0,0},
                    {0,0,0,0}
            },
            {
                    {1,0,0},
                    {1,0,0},
                    {1,1,0}
            },
            {
                    {1,1,1},
                    {0,0,1},
                    {0,0,0}
            },
            {
                    {1,1,0},
                    {0,1,1},
                    {0,0,0}

            },
            {
                    {1,0,0},
                    {1,1,0},
                    {0,1,0}

            },
            {
                    {1,1},
                    {1,1}

            }
    };

    public Blocks(int[][] block){
        this.myblock=block;
//        need_trans=random.nextBoolean();
    }

    /**
     * 当前列是否为空
     * @param col 列
     * @param ablock 和this.myblock尺寸一样的数组
     * @return 是否为空
     */
    private boolean ColIsEmpty(int col,int[][] ablock){
        boolean is_empty=true;
        for(int j=0;j<ablock[0].length;j++){
            if(ablock[j][col]==1){
                is_empty=false;
                break;
            }
        }
        return is_empty;
    }

    /**
     * 当前列是否为空
     * @param col 列
     * @return 是否为空
     */
    private boolean ColIsEmpty(int col){
        boolean is_empty=true;
        for(int j=0;j<this.myblock[0].length;j++){
            if(this.myblock[j][col]==1){
                is_empty=false;
                break;
            }
        }
        return is_empty;
    }

    private boolean RowIsEmpty(int row){
        boolean is_empty=true;
        for(int i=0;i<this.myblock.length;i++){
            if(this.myblock[row][i]==1){
                is_empty=false;
                break;
            }
        }
        return is_empty;
    }


    public void Rot(int[][] map,int current_x,int current_y){
        //如果能旋转再转
//        if()

        int[][] new_block=new int[this.myblock.length][this.myblock.length];
        //旋转
        for(int i=0;i<this.myblock.length;i++){
            for(int j=0;j<this.myblock.length;j++){
                new_block[j][i]=this.myblock[this.myblock.length-i-1][j];
            }
        }
//        this.myblock=new_block;
        //平移
        for(int i=0;i<new_block.length;i++){//遍历列
            if(!ColIsEmpty(i,new_block)){//找到第一个非空列时
                if(i!=0){
                    //搬数据
                    for(int j=0;j<new_block.length;j++){//遍历行
                        for(int k=0;k<new_block.length-i;k++){//遍历空列
                            new_block[j][k]=new_block[j][i+k];
                        }
                        //清空末尾数据
                        for(int k=new_block.length-i;k<new_block.length;k++){
                            new_block[j][k]=0;
                        }
                    }
                }
                break;//退出外层遍历列
            }
        }
        //计算超出和重叠
        try{
            for(int i=0;i<new_block.length;i++){
                for(int j=0;j<new_block.length;j++){
                    if(map[current_y+j][current_x+i]!=0&&new_block[j][i]!=0){
                        return;
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e){
            return;
        }
        this.myblock=new_block;


    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color){
        this.color=color;
    }

    public int getMaxWidth(){
        for(int i=0;i<this.myblock[0].length;i++){
            if(ColIsEmpty(i)){
                return i;
            }
        }
        return this.myblock[0].length;
    }

    public int getMaxHeight(){
        for(int i=0;i<this.myblock.length;i++){
            if(RowIsEmpty(i)){
                return i;
            }
        }
        return this.myblock.length;
    }

    public int getBlockLen(){
        return this.myblock[0].length;
    }




//    private static final double[] rate={
//            1,1,1,1,1,1,1
//    };

    private static int[][] Trans(int[][] ablock){
        int[][] res=new int[ablock[0].length][ablock[0].length];
        for(int i=0;i<ablock[0].length;i++){
            for(int j=0;j<ablock[0].length;j++){
                res[i][j]=ablock[j][i];
            }
        }
        return res;
    }

    public static Blocks GetNextBlock(){
        int[][] block=blocks[random.nextInt(blocks.length)];
        if(random.nextBoolean()){
            block=Trans(block);
        }
        Blocks res=new Blocks(block);
        return res;
    }


}
