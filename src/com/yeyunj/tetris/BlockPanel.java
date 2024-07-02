package com.yeyunj.tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BlockPanel extends JPanel {
    private final int x_blocks_count = 10;
    private final int y_blocks_count = 20;
    private int x_pixel_offset = 0;
    private int y_pixel_offset = 0;

    //0为空元素，显示为白色，先索引的是纵向的，即y，然后是横向的x
    private int[][] map = new int[y_blocks_count][x_blocks_count];

    private int block_width;
    Blocks current_block;

    private int current_x = x_blocks_count / 2;
    //    private int current_y=-current_block.getBlockLen();
    private int current_y = 0;

    private int predicted_y=0;

    private int score = 0;
    Blocks next_block;

    private int generate_at_bottom_seconds_count = 0;

    private boolean is_paused=false;
    private boolean show_predicted_location=true;

    private static final int per_fix_score=5;//每次成功固定方块后加的分
    private static final double start_rate = 20; //只消掉一行加的分
    private static final double clear_row_rate = 2; //每多消掉一行增加的倍数
    private static final double rainbow_coefficient=0.4;//彩虹方块奖励系数
    private static final double explode_radius=2;

    private static final Random block_random = new Random();

    public static final double rainbow_rate=0.05;
    public static final Color rainbow_color = new Color(255, 0, 255);

    public static final Color predict_color=new Color(252,216,239);

    //每90秒后会生成一层方块
    public static int generate_at_buttom_after_seconds=90;

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
            Color.GRAY,
            rainbow_color
    };

    public BlockPanel() {
        this.ResetBlock();
    }

    public static Color SampleColor() {
        double rainbow_sample= block_random.nextDouble();
        if(rainbow_sample<rainbow_rate){
            return rainbow_color;
        }
        return color_map[block_random.nextInt(1, color_map.length-1)];
    }

    @Override
    public void paint(Graphics g) {
        //横向有空位
        if (this.getHeight() / y_blocks_count < this.getWidth() / x_blocks_count) {
            block_width = this.getHeight() / y_blocks_count;
            x_pixel_offset = (this.getWidth() - x_blocks_count * block_width) / 2;
        }
        //纵向有空位
        else {
            block_width = this.getWidth() / x_blocks_count;
            y_pixel_offset = (this.getHeight() - y_blocks_count * block_width) / 2;
        }

        //画粗线条
        g.setColor(Color.ORANGE);
        //竖线
        for (int i = 0; i <= x_blocks_count; i++) {
            int now_x = x_pixel_offset + block_width * i;
            for (int j = -2; j < 3; j++) {
                if (j != 0) {
                    g.drawLine(now_x + j, y_pixel_offset, now_x + j, y_pixel_offset + y_blocks_count * block_width);
                }
            }
        }
        //横线
        for (int i = 0; i <= y_blocks_count; i++) {
            int now_y = y_pixel_offset + block_width * i;
            for (int j = -2; j < 3; j++) {
                if (j != 0) {
                    g.drawLine(x_pixel_offset, now_y + j, x_pixel_offset + x_blocks_count * block_width, now_y + j);
                }
            }
        }

        //画细线条
        g.setColor(Color.BLACK);
        //竖线
        for (int i = 0; i <= x_blocks_count; i++) {
            int now_x = x_pixel_offset + block_width * i;
            g.drawLine(now_x, y_pixel_offset, now_x, y_pixel_offset + y_blocks_count * block_width);

        }
        //横线
        for (int i = 0; i <= y_blocks_count; i++) {
            int now_y = y_pixel_offset + block_width * i;
            g.drawLine(x_pixel_offset, now_y, x_pixel_offset + x_blocks_count * block_width, now_y);

        }

        //画存在的方块
        for (int i = 0; i < x_blocks_count; i++) {
            for (int j = 0; j < y_blocks_count; j++) {
                if (map[j][i] != -1) {
                    g.setColor(color_map[map[j][i]]);
                    DrawXYBlock(g, i, j);
                }
            }
        }
        //画预测的位置
        if(show_predicted_location){
            g.setColor(predict_color);
            for (int i = 0; i < current_block.getBlockLen(); i++) {
                for (int[] point : current_block.getData().getDatas()) {
                    DrawXYBlock(g, point[0] + current_x, point[1] + predicted_y);
                }
            }
        }

        //画当前下落的方块
        g.setColor(current_block.getColor());
        for (int i = 0; i < current_block.getBlockLen(); i++) {
            for (int[] point : current_block.getData().getDatas()) {
                DrawXYBlock(g, point[0] + current_x, point[1] + current_y);
            }
        }
    }

    /**
     * 点是否在地图内
     *
     * @param x
     * @param y
     * @return 是否在地图内
     */
    private boolean insideOfMap(int x, int y) {
        return (x >= 0 && x < x_blocks_count && y >= 0 && y < y_blocks_count);
    }

    /**
     * 点是否在地图内
     *
     * @param x
     * @param y
     * @return 是否在地图内
     */
    private boolean insideOfMapWithoutTop(int x, int y) {
        return (x >= 0 && x < x_blocks_count && y < y_blocks_count);
    }

    private boolean canPlaceBlock(int x, int y) {
        return insideOfMap(x, y) && (map[y][x] == 0);
    }

    public void switchPaused(){
        is_paused=!is_paused;
    }

    public boolean isPaused() {
        return is_paused;
    }

    public void switchShowPredictedLocation(){
        show_predicted_location=!show_predicted_location;
    }

    /**
     * 如果超出范围仍然会认为能放方块，只有真正有方块才会返回false，一般这个函数只在每个tick的下移函数才会调用，以防止生成时生成在外面的情况
     *
     * @param x
     * @param y
     * @return 有没有方块
     */
    private boolean justHasBlock(int x, int y) {
        if (insideOfMap(x, y)) {
            return map[y][x] != 0;
        }
        return false;
    }

    /**
     * 如果碰撞了返回true，这个位置没有碰撞则为false，不用管范围问题，越界只会返回false保证不报错
     *
     * @param x         基坐标x
     * @param y         基坐标y
     * @param blockData 方块数据
     * @return 是否碰撞
     */
    public boolean isCrashed(int x, int y, BlockData blockData) {
        for (int[] point : blockData.getDatas()) {
            int offset_x = point[0] + x;
            int offset_y = point[1] + y;
            if (!canPlaceBlock(offset_x, offset_y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如果碰撞了返回true，这个位置没有碰撞则为false，不用管范围问题，越界只会返回true保证不报错
     *
     * @param x         基坐标x
     * @param y         基坐标y
     * @param blockData 方块数据
     * @return 是否碰撞
     */
    public boolean isCrashedWithoutTop(int x, int y, BlockData blockData) {
        for (int[] point : blockData.getDatas()) {
            int offset_x = point[0] + x;
            int offset_y = point[1] + y;
            if (!insideOfMapWithoutTop(offset_x, offset_y)) {
                return true;
            }
            //前面那个if判断过了大部分，但y是否在map内没判断，因此这里判断一下，防止越界报错
            if (offset_y >= 0 && !canPlaceBlock(offset_x, offset_y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 只检测下方的方块，性能比isCrashedWithoutTop方法好
     * @return 是否可以下移
     */
    public boolean canMoveDown() {
        BlockData moved_data = current_block.getMoveTowardsBlocks(current_x, current_y + 1);
        for (int[] point : moved_data.getDatas()) {
            if (justHasBlock(point[0], point[1]) || !(point[1] < y_blocks_count)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 只检测下方的方块，性能比isCrashedWithoutTop方法好
     * @param x x坐标
     * @param y y坐标
     * @return 是否可以下移
     */
    public boolean canMoveDownXY(int x,int y) {
        BlockData moved_data = current_block.getMoveTowardsBlocks(x, y + 1);
        for (int[] point : moved_data.getDatas()) {
            if (justHasBlock(point[0], point[1]) || !(point[1] < y_blocks_count)) {
                return false;
            }
        }
        return true;
    }

    public void updatePredictedY(){
        predicted_y=current_y;
        while(canMoveDownXY(current_x,predicted_y)){
            predicted_y++;
        }
    }

    public void moveBlockToPredictedLocation(){
        current_y=predicted_y;
    }

    /**
     * 到底了返回true，否则返回false
     *
     * @return 是否到底
     */
    public boolean TryMoveDown() {
        if (canMoveDown()) {
            current_y++;
            return false;
        }
        return true;
    }

    public void incSecondCount(){
        generate_at_bottom_seconds_count++;
    }

    /**
     * 全部方块上移，并且在底部生成方块，如果失败返回true。必须先整体上移，再执行blocks的下移，不然会导致上移后方块不知道自己碰撞了，会造成逻辑非常复杂
     * @return
     */
    public boolean generateBlockAtBottomAndDetectFailure(){
        boolean is_failure=false;

        //如果该生成方块了
        if(generate_at_bottom_seconds_count >=generate_at_buttom_after_seconds){
            generate_at_bottom_seconds_count =0;
            is_failure=moveAllBlockUpOneCeilAndDetectFailure();
            generateRandomBlockAtBottom();
        }
        return is_failure;
    }

    /**
     * 把这行上方的方块往下移一格
     * @param col 哪行
     */
    private void moveBlockAboveDownOneCeil(int col){
        //遍历每行
        for (int ti = col - 1; ti >= 0; ti--) {
            //遍历每列
            for (int tj = 0; tj < x_blocks_count; tj++) {
                //上行移下来
                this.map[ti + 1][tj] = this.map[ti][tj];
                //上一行清空
                this.map[ti][tj] = 0;
            }
        }
    }

    /**
     * 某一行是否有方块
     * @param row 哪行
     * @return 是否有方块
     */
    private boolean isRowHasBlock(int row){
        for(int i=0;i<x_blocks_count;i++){
            if(map[row][i]!=0){
                return true;
            }
        }
        return false;
    }

    /**
     * 计算这一列有几个方块
     * @param colum 哪列
     * @return 几个方块
     */
    private int countBlocksInCol(int colum){
        int count=0;
        for(int i=0;i<y_blocks_count;i++){
            if(map[i][colum]!=0){
                count++;
            }
        }
        return count;
    }


    /**
     * 把所有方块往上移一格，如果最上面有方块则失败，失败返回true
     * @return 是否失败
     */
    private boolean moveAllBlockUpOneCeilAndDetectFailure(){
        for(int i=0;i<x_blocks_count;i++){
            if(isRowHasBlock(0)){
                return true;
            }
        }
        //遍历每行
        for (int row = 1; row <y_blocks_count; row++) {
            //遍历每列
            for (int tj = 0; tj < x_blocks_count; tj++) {
                //下一行移上去
                this.map[row - 1][tj] = this.map[row][tj];
                //上一行清空
                this.map[row][tj] = 0;
            }
        }
        //最下面那行清空
        clearRow(y_blocks_count-1);
        return false;
    }

    private void generateRandomBlockAtBottom(){
        Random rand = new Random();
        for(int i=0;i<x_blocks_count;i++){
            if(rand.nextBoolean()){
                map[y_blocks_count-1][i] = color_map.length-2;
            }
        }
    }

    /**
     * 当方块固定时该做的事，包括把移动的方块固定到map，删除那些应该消失的方块，切换下一个方块。失败返回true
     * @return 是否失败
     */
    public boolean whenBlocksFixShouldDoAndDetectFailure(){
        boolean failure=FixCurrentBlockAndDetectFailure();
        if (!failure) {
            DetectAndDeleteLine();
            ChangeBlock();
        }
        return failure;
    }


    /**
     * 固定当前的blocks并且检测是否输了，输了返回true
     *
     * @return 是否输了
     */
    public boolean FixCurrentBlockAndDetectFailure() {
        //确定固定后的颜色
        int index = getColorIndex(this.current_block.getColor());
        if (index == -1) {
            index = 0;
        }
        BlockData moved_block_data = this.current_block.getMoveTowardsBlocks(current_x, current_y);
        for (int[] point : moved_block_data.getDatas()) {
            if (point[1] < 0) {
                return true;
            }
            this.map[point[1]][point[0]] = index;
        }
        //没有失败的话每次固定后都加分
        this.score+=per_fix_score;

        return false;
    }

    private void DetectAndDeleteLine() {
        //统计消除了几行
        int count_full_line = 0;
        //统计哪几行是被消除的
        boolean[] full_row=new boolean[this.y_blocks_count];
        //统计所有消除行中彩虹方块的数量
        ArrayList<int[]> rainblocks_location=new ArrayList<>();
        //总共炸了多少方块
        int exploded_blocks_count=0;

        for (int i = 0; i < this.y_blocks_count; i++) {
            boolean is_full = true;
            for (int j = 0; j < this.x_blocks_count; j++) {
                if (this.map[i][j] == 0) {
                    is_full = false;
                    break;
                }
            }
            full_row[i] = is_full;
            //如果这行满了
            if (is_full) {
                count_full_line++;
                //检查这一行是否包含彩虹色方块
                for (int j = 0; j < this.x_blocks_count; j++) {
                    if (color_map[map[i][j]].equals(rainbow_color)) {
//                        need_clear_col[j] = true;
                        //添加进彩虹方块列表中
                        rainblocks_location.add(new int[]{j,i});
                    }
                }
                full_row[i] = true;
            }

        }
        //对所有彩虹方块执行爆炸
        for(int[] location:rainblocks_location){
            exploded_blocks_count+=chainExplode(location[0],location[1],explode_radius);
        }

        //爆炸完后再下移每行，不然逻辑错误
        for(int i=0;i<y_blocks_count;i++){
            if(full_row[i]){
                moveBlockAboveDownOneCeil(i);
            }
        }
        //如果消掉了行
        if (count_full_line != 0) {
            //加分
            this.score += start_rate * Math.pow(clear_row_rate, count_full_line - 1);
        }

//        int cleared_blocks=0;
//        for(int i=0;i<x_blocks_count;i++){
//            if(need_clear_col[i]){
//                //消掉的方块越多加的分越多
//                cleared_blocks+=countBlocksInCol(i);
//                clearColumn(i);
//            }
//        }
        //爆炸加的分
        this.score += rainbow_coefficient*exploded_blocks_count*exploded_blocks_count;
    }

    private void clearMap(){
        for (int i = 0; i < y_blocks_count; i++) {
            for (int j = 0; j < x_blocks_count; j++) {
                this.map[i][j] = 0;
            }
        }
    }

    private void clearColumn(int columnIndex) {
        for (int i = 0; i < this.y_blocks_count; i++) {
            this.map[i][columnIndex] = 0;
        }
    }

    private void clearRow(int rowIndex) {
        for (int i = 0; i < this.x_blocks_count; i++) {
            this.map[rowIndex][i] = 0;
        }
    }

    /**
     * 爆炸，返回炸了多少方块
     * @param x x坐标
     * @param y y坐标
     * @param radius 半径
     * @return 炸了几个方块
     */
    private int explode(int x,int y,int radius){
        int num=0;
        //先遍历y轴
        for(int y_offset=-radius;y_offset<radius+1;y_offset++){
            int delta_x= (int) Math.round(Math.sqrt(Math.pow(radius,2)-Math.pow(y_offset,2)));
            for(int x_offset=-delta_x;x_offset<delta_x+1;x_offset++){
                int target_x=current_x+x_offset,target_y=current_y+y_offset;
                if(insideOfMap(target_x,target_y)&&map[target_y][target_x]!=0){
                    num++;
                    map[target_y][target_x]=0;
                }
            }
        }
        return num;
    }

    /**
     * 爆炸，返回炸了多少方块
     * @param x x坐标
     * @param y y坐标
     * @param radius 半径
     * @return 炸了几个方块
     */
    private int chainExplode(int x,int y,double radius){
        int round_radius= (int) Math.round(radius);
        int num=0;
        //先遍历y轴
        for(int y_offset=-round_radius;y_offset<round_radius+1;y_offset++){
            int delta_x= (int) Math.round(Math.sqrt(Math.pow(round_radius,2)-Math.pow(y_offset,2)));
            for(int x_offset=-delta_x;x_offset<delta_x+1;x_offset++){
                int target_x=x+x_offset,target_y=y+y_offset;
                if(insideOfMap(target_x,target_y)&&map[target_y][target_x]!=0){
                    //如果是彩虹方块则继续爆炸
                    if(color_map[map[target_y][target_x]].equals(rainbow_color)){
                        //先清除本方块，防止重复检测导致爆栈
                        map[target_y][target_x]=0;
                        num+=chainExplode(target_x,target_y,radius);
                    }
                    //如果不是则直接清除
                    else{
                        num++;
                        map[target_y][target_x]=0;
                    }
                }
            }
        }
        return num;
    }

    private Blocks generateNextBlocks() {
        Blocks ret = Blocks.GetNextBlock();
        Color samples_color=BlockPanel.SampleColor();
        ret.setColor(samples_color);
        if(samples_color.equals(rainbow_color)){
            ret.setRainbow(true);
        }
        return ret;
    }

    public void ResetBlock() {
        this.next_block = generateNextBlocks();
        this.ChangeBlock();
    }

    private void ChangeBlock() {
        this.current_block = this.next_block;
        this.next_block = generateNextBlocks();
        this.current_x = getDefaultX();
        this.current_y = getDefaultY();
    }

    public void Reset() {
        this.score = 0;
        this.clearMap();;
        this.ResetBlock();
        this.generate_at_bottom_seconds_count =0;
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
        g.fillRect(x_pixel_offset + x * block_width + 3, y_pixel_offset + y * block_width + 3, block_width - 5, block_width - 5);
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
        return x_blocks_count / 2;
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

    public int getCurrentUserMaxScore(){
        if(SwingUtilities.getWindowAncestor(this) instanceof MainFrame mainFrame){
            return mainFrame.getFromTetris().userData.getMax_score();
        }
        return 0;
    }

    public void NextStep(BlockAction action) {
        switch (action) {
            case MoveLeft -> {
                if (!isCrashedWithoutTop(current_x, current_y, current_block.getMoveLeftBlocks())) {
                    this.current_x--;
                }
            }
            case MoveRight -> {
                if (!isCrashedWithoutTop(current_x, current_y, current_block.getMoveRightBlocks())) {
                    this.current_x++;
                }
            }
            case Rot -> {
                BlockData rotted_block = this.current_block.getRotClockWiseBLocks();
                if (!isCrashedWithoutTop(current_x, current_y, rotted_block)) {
                    this.current_block.setData(rotted_block);
                }
            }
        }
    }
}