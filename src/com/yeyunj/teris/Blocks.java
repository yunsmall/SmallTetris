package com.yeyunj.teris;

import java.awt.*;
import java.util.Random;

public class Blocks {
    private Color color=Color.YELLOW;
    private boolean isRainbow = false;

    private BlockData data;

    public BlockData getData() {
        return data;
    }

    private static final Random random = new Random();

    public final static int[][][] blocks = {
            {
                    {0, 0},
                    {1, 0},
                    {0, -1},
                    {-1, 0}
            },
            {
                    {-2, 0},
                    {-1, 0},
                    {0, 0},
                    {1, 0}
            },
            {
                    {-1, 0},
                    {0, 0},
                    {1, 0},
                    {2, 0}
            },
            {
                    {0, -1},
                    {0, 0},
                    {0, 1},
                    {1, 1}
            },
            {
                    {-1, 0},
                    {0, 0},
                    {1, 0},
                    {1, 1}
            },
            {
                    {-1, 0},
                    {0, 0},
                    {0, 1},
                    {1, 1}

            },
            {
                    {0, -1},
                    {0, 0},
                    {1, 0},
                    {1, 1}

            },
            {
                    {0, 0},
                    {1, 0},
                    {0, 1},
                    {1, 1}

            }
    };

    public Blocks(BlockData block) {
        this.data = block;
    }

    public Color getColor() {
        return color;
    }

    public boolean isRainbow() {
        return isRainbow;
    }

    public void setRainbow(boolean rainbow) {
        isRainbow = rainbow;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setData(BlockData data) {
        this.data = data;
    }

    public int getBlockLen() {
        return this.data.getBlocksCount();
    }

    public BlockData getRotClockWiseBLocks() {
        int[][] new_blocks = new int[this.data.getBlocksCount()][];
        int[][] my_block_data = this.data.getDatas();
        for (int i = 0; i < this.data.getBlocksCount(); i++) {
            new_blocks[i] = new int[]{-my_block_data[i][1], my_block_data[i][0]};
        }
        return new BlockData(new_blocks);
    }

    public BlockData getRotAntiClockWiseBLocks() {
        int[][] new_blocks = new int[this.data.getBlocksCount()][];
        int[][] my_block_data = this.data.getDatas();
        for (int i = 0; i < this.data.getBlocksCount(); i++) {
            new_blocks[i] = new int[]{my_block_data[i][1], -my_block_data[i][0]};
        }
        return new BlockData(new_blocks);
    }

    public BlockData getMoveTowardsBlocks(int x_offset, int y_offset) {
        int[][] new_blocks = new int[this.data.getBlocksCount()][];
        int[][] my_block_data = this.data.getDatas();
        for (int i = 0; i < this.data.getBlocksCount(); i++) {
            new_blocks[i] = new int[]{my_block_data[i][0] + x_offset, my_block_data[i][1] + y_offset};
        }
        return new BlockData(new_blocks);
    }

    public BlockData getMoveDownBlocks() {
        return getMoveTowardsBlocks(0, 1);
    }

    public BlockData getMoveRightBlocks() {
        return getMoveTowardsBlocks(1, 0);
    }

    public BlockData getMoveLeftBlocks() {
        return getMoveTowardsBlocks(-1, 0);
    }

    public static Blocks GetNextBlock() {
        BlockData blockData = new BlockData(blocks[random.nextInt(blocks.length)].clone());
        if (random.nextBoolean()) {
            blockData.overturn();
        }

        return new Blocks(blockData);
    }

    public int getMaxHeight() {
        return data.getMaxHeight();
    }

    public int getMaxWidth() {
        return data.getMaxWidth();
    }
}
