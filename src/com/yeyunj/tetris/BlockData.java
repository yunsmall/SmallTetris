package com.yeyunj.tetris;

public class BlockData {

    //格式：第一层是每一个点，第二层为此点的坐标，横纵坐标按UI的基底的方向
    private int[][] datas;

    public BlockData(int[][] block_datas) {
        this.datas = block_datas;
    }

    public int[][] getDatas() {
        return datas;
    }

    public void setDatas(int[][] datas) {
        this.datas = datas;
    }

    public int getBlocksCount(){
        return datas.length;
    }

    public void overturn(){
        int[][] res=new int[datas.length][2];
        for(int i=0;i<datas.length;i++){
            res[i][0]=datas[i][1];
            res[i][1]=datas[i][0];
        }
        datas=res;
    }

    public int getMaxHeight(){
        int min_height=Integer.MAX_VALUE;
        int max_height=Integer.MIN_VALUE;
        for(int[] point:datas){
            if(point[1]<min_height){
                min_height=point[1];
            }
            if(point[1]>max_height){
                max_height=point[1];
            }
        }
        return max_height-min_height+1;
    }

    public int getMaxWidth(){
        int min_width=Integer.MAX_VALUE;
        int max_width=Integer.MIN_VALUE;
        for(int[] point:datas){
            if(point[0]<min_width){
                min_width=point[0];
            }
            if(point[0]>max_width){
                max_width=point[0];
            }
        }
        return max_width-min_width+1;
    }

    public int[] getLeftUp(){
        int left=Integer.MAX_VALUE;
        int up=Integer.MAX_VALUE;
        for(int i=0;i<datas.length;i++){
            if(datas[i][0]<left){
                left=datas[i][0];
            }
            if(datas[i][1]<up){
                up=datas[i][1];
            }
        }
        return new int[]{left,up};
    }
}
