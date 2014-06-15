package com.example.makemap;

import java.util.Random;

/**
 * Created by Administrator on 2014/6/15.
 */
public class MadeInArray {
    private static final int DEFAULT_WIDTH = 14;
    private static final int DEFAULT_HEIGHT = 8;

    private int[][] mapArray;

    public MadeInArray() {
        this.mapArray = new int[DEFAULT_WIDTH][DEFAULT_HEIGHT];
    }

    public MadeInArray(int width, int height) {
        this.mapArray = new int[width][height];
    }

    /**
     * 加入随机障碍物的坐标
     */
    private void joinBarrierPoint() {
        //随机加入4-8个障碍物（只占一个单元格）
        int barrierCount = new Random().nextInt(20) + 4;
        int mapWidth = mapArray.length;
        int mapHeight = mapArray[0].length;
        for (int i = 0; i < barrierCount; i++) {
            int point = new Random().nextInt(mapWidth * mapHeight);
            int pointWidth = point / mapWidth;
            int pointHeight = point % mapWidth;
            if (mapArray[pointWidth][pointHeight] == 0) {
                mapArray[pointWidth][pointHeight] = 2;
            } else {
                barrierCount += 1;
            }
        }
    }

    /**
     * 加入路径的几个转角点的随机坐标
     */
    private void joinCornerPoint(int cornerCount) {
        //随机加入4-8个随机转角点（只占一个单元格）
        int barrierCount = new Random().nextInt(5) + 4;
        int mapWidth = mapArray.length;
        int mapHeight = mapArray[0].length;
        for (int i = 0; i < barrierCount; i++) {
            int point = new Random().nextInt(mapWidth * mapHeight);
            int pointWidth = point / mapWidth;
            int pointHeight = point % mapWidth;
            if (mapArray[pointWidth][pointHeight] == 0) {
                mapArray[pointWidth][pointHeight] = 1;
            } else {
                barrierCount += 1;
            }
        }
    }

    /**
     * 根据转角点和障碍物计算出路径加入到地图二维数组矩阵中
     */
    private void getThePath() {
        AStar aStar = new AStar(mapArray, mapWidth(), mapHeigh());
        for (int i = 0; i < mapWidth(); i++) {
            for (int j = 0; j < mapHeigh(); j++) {

                aStar.search()
            }
        }
    }

    public int[][] getMapArray() {
        return mapArray;
    }

    private int mapWidth() {
        if (mapArray != null) {
            return mapArray.length;
        }
        return 0;
    }

    private int mapHeigh() {
        if (mapArray != null && mapArray[0] != null) {
            return mapArray[0].length;
        }
        return 0;
    }

    public static void main(String[] args) {
        MadeInArray map = new MadeInArray();
        System.out.println(map.getMapArray()[0].length);
    }
}
