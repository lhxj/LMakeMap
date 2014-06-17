package com.example.makemap;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2014/6/15.
 */
public class MadeInArray {
    private static final String TAG = "MadeInArray";
    //地图默认宽高
    private static final int DEFAULT_WIDTH = 8;
    private static final int DEFAULT_HEIGHT = 14;
    //地图中默认的障碍物为99
    private static final int DEFAULT_BARRIER = 99;
    //地图中默认的障碍物数量范围 （如：5至20个随机的，值为15）
    private static final int DEFAULT_BARRIER_COUNT = 20;
    //地图参数数组结构
    private int[][] mapArray;
    //地图路径数量默认设置为80步以上就可以了
    private static final int DEFAULT_PATH_COUNT = 80;

    public MadeInArray() {
        this.mapArray = new int[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        init();
    }

    public MadeInArray(int width, int height) {
        this.mapArray = new int[width][height];
        init();
    }

    private void init() {
        //首先初始化地图参数以后加入障碍物
        joinBarrierPoint();
        //然后再根据其绘制地图路径
        int pathCount = 0;
    }

    /**
     * 加入随机障碍物的坐标
     */
    private void joinBarrierPoint() {
        //随机加入4-8个障碍物（只占一个单元格）
        int barrierCount = new Random().nextInt(DEFAULT_BARRIER_COUNT) + 5;
        int mapWidth = mapWidth();
        int mapHeight = mapHeigh();
        for (int i = 0; i < barrierCount; i++) {
            int point = new Random().nextInt(mapWidth * mapHeight);
            int pointHeight = point / mapWidth;
            int pointWidth = point % mapWidth;
            if (mapArray[pointWidth][pointHeight] == 0) {
                mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER;
            } else {
                barrierCount += 1;
            }
        }
    }

    /**
     * 根据转角点和障碍物计算出路径加入到地图二维数组矩阵中
     */
    private int getThePath(Point x, int count) {
        AStar aStar = new AStar(mapArray, mapWidth(), mapHeigh());
        Point y = new Point(new Random().nextInt(5) + x.getX() - 2, new Random().nextInt(mapHeigh()));
        int flag = aStar.search(x.getX(), x.getY(), y.getX(), y.getY(), count);
        if (flag == -1 || flag == 0) {
            System.out.println("-----计算出两点之间的路径出现数据问题或没有计算出两点之间的路径!");
            flag = getThePath(x, count);
        }
        return flag;
    }

    private Point initOrigin() {
        int x = 0;
        int y = new Random().nextInt(mapHeigh());
        mapArray[x][y] = 1;
        return new Point(x, y);
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
        int[][] array = map.getMapArray();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j] == 0) {
                    System.out.print("〓");
                    //} else if (array[i][j] == 1) {
                    //    System.out.print("11");
                    // } else if (array[i][j] == 2) {
                    //     System.out.print("※");
                } else if (array[i][j] == 10) {
                    System.out.print("00");
                } else {
                    System.out.print(array[i][j] + "" + array[i][j]);
                }
            }
            System.out.println();
        }
    }
}

class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "x=" + this.x + "  y=" + this.y;
    }
}