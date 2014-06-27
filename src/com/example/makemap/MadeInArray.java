package com.example.makemap;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 绘制地图参数
 * Created by Administrator on 2014/6/15.
 */
public class MadeInArray {
    private static final String TAG = "MadeInArray";
    //地图默认宽高
    private static final int DEFAULT_WIDTH = 16;
    private static final int DEFAULT_HEIGHT = 10;
    //地图中默认的障碍物为99
    public static final int DEFAULT_BARRIER_1 = 9;
    public static final int DEFAULT_BARRIER_2 = 8;
    public static final int DEFAULT_BARRIER_4 = 7;
    public static final int DEFAULT_PATH = 1;
    //地图默认重绘次数（大于该次数还无法找到正确的坐标进行绘制，则需要重新生成新的地图，以免陷入死循环）
    private static final int DRAW_RETRY_COUNT = 4;
    //地图参数数组结构
    private int[][] mapArray;
    //地图路径数量默认设置为80步以上就可以了
    private static final int DEFAULT_PATH_COUNT = 60;
    //地图绘制多次路径后剩余数量
    private int pathOdd;
    //路径点情况记录
    private LinkedList<PathPoint> pathList;
    // 随机对象值
    private Random random = new Random();

    // 地图以随机4个点作为依据判断主要方向为哪几个：0-代表左上角即以右下为主  1-代表右上角即以左下为主
    // 2-代表右下角即以左上为主 3-代表左下角即以右上为主
    private final int orientation = random.nextInt(4);
    // 确定起始点位置，即相反方向为终点位置
    private Point origin;

    public MadeInArray() {
        this.mapArray = new int[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        this.pathOdd = DEFAULT_PATH_COUNT;
    }

    public MadeInArray(int width, int height) {
        this.mapArray = new int[width][height];
        this.pathOdd = DEFAULT_PATH_COUNT;
    }

    /**
     * 初始化绘制随机地图参数数据
     *
     * @return false-失败 true-成功
     */
    public boolean init() {
        pathList = new LinkedList<PathPoint>();
        initOriginAndDestination();
        //首先初始化地图参数以后加入障碍物
        joinBarrierPoint();
        //然后再根据其绘制地图路径
        pathList.add(new PathPoint(origin.getX(), origin.getY()));
        Point initPoint = origin;
        while (pathOdd > 0) {
            initPoint = getThePath(initPoint, DEFAULT_PATH, 1);
            if (initPoint == null) {
                // 这代表着地图通过多次绘制随机点找不到适合的路径，所以重新绘制地图
                return false;
            } else {
                mapArray[initPoint.getX()][initPoint.getY()] = 1;
            }
        }
        return true;
    }

    /**
     * 加入随机障碍物的坐标
     */
    private void joinBarrierPoint() {
        //随机加入N个障碍物（3*4 6*2 12*1 左右）
        int b4Count = random.nextInt(4) + 1;
        int b2Count = random.nextInt(8) + 1;
        int b1Count = random.nextInt(16) + 1;
        int mapWidth = mapWidth();
        int mapHeight = mapHeigh();
        for (int i = 0; i < b4Count; i++) {
            //设置4的障碍
            int point = random.nextInt(mapWidth * mapHeight);
            int pointHeight = point / mapWidth;
            int pointWidth = point % mapWidth;
            //找到设置点以后需要判断附近4点是否可设置
            if (mapArray[pointWidth][pointHeight] == 0 && mapArray[(pointWidth + 1) % mapWidth][pointHeight] == 0
                    && mapArray[pointWidth][(pointHeight + 1) % mapHeight] == 0 && mapArray[(pointWidth + 1) % mapWidth][(pointHeight + 1) % mapHeight] == 0) {
                mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_4;
                mapArray[(pointWidth + 1) % mapWidth][pointHeight] = DEFAULT_BARRIER_4;
                mapArray[pointWidth][(pointHeight + 1) % mapHeight] = DEFAULT_BARRIER_4;
                mapArray[(pointWidth + 1) % mapWidth][(pointHeight + 1) % mapHeight] = DEFAULT_BARRIER_4;
            } else {
                b4Count += 1;
            }
        }
        for (int i = 0; i < b2Count; i++) {
            //设置2的障碍
            int point = random.nextInt(mapWidth * mapHeight);
            int pointHeight = point / mapWidth;
            int pointWidth = point % mapWidth;
            //找到设置点以后需要判断附近2点是否可设置
            int direction = random.nextInt(4);
            switch (direction) {
                case 0:
                    if (mapArray[pointWidth][pointHeight] == 0
                            && mapArray[(pointWidth + 1) % mapWidth][pointHeight] == 0) {
                        mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_2;
                        mapArray[(pointWidth + 1) % mapWidth][pointHeight] = DEFAULT_BARRIER_2;
                    } else {
                        b2Count += 1;
                    }
                    break;
                case 1:
                    if (mapArray[pointWidth][pointHeight] == 0 && mapArray[pointWidth][Math.abs(pointHeight - 1)] == 0) {
                        mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_2;
                        mapArray[pointWidth][Math.abs(pointHeight - 1)] = DEFAULT_BARRIER_2;
                    } else {
                        b2Count += 1;
                    }
                    break;
                case 2:
                    if (mapArray[pointWidth][pointHeight] == 0 && mapArray[pointWidth][(pointHeight + 1) % mapHeight] == 0) {
                        mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_2;
                        mapArray[pointWidth][(pointHeight + 1) % mapHeight] = DEFAULT_BARRIER_2;
                    } else {
                        b2Count += 1;
                    }
                    break;
                case 3:
                    if (mapArray[pointWidth][pointHeight] == 0 && mapArray[Math.abs(pointWidth - 1)][pointHeight] == 0) {
                        mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_2;
                        mapArray[Math.abs(pointWidth - 1)][pointHeight] = DEFAULT_BARRIER_2;
                    } else {
                        b2Count += 1;
                    }
                    break;
                default:
                    break;
            }
        }
        for (int i = 0; i < b1Count; i++) {
            //设置1的障碍
            int point = random.nextInt(mapWidth * mapHeight);
            int pointHeight = point / mapWidth;
            int pointWidth = point % mapWidth;
            //找到设置点以后需要判断1点是否可设置
            if (mapArray[pointWidth][pointHeight] == 0) {
                mapArray[pointWidth][pointHeight] = DEFAULT_BARRIER_1;
            } else {
                b1Count += 1;
            }
        }
    }

    /**
     * 根据转角点和障碍物计算出路径加入到地图二维数组矩阵中
     */
    private Point getThePath(Point x, int pathNum, int retry) {
        if (retry >= DRAW_RETRY_COUNT) {
            return null;
        }
        AStar aStar = initAStar();
        Point y = getNextPoint(x);
        List<PathPoint> list = aStar.search(x.getX(), x.getY(), y.getX(), y.getY(), pathNum);
        if (list == null || list.size() <= 0) {
            System.out.println("MadeInArray-----计算出两点之间的路径出现数据问题或没有计算出两点之间的路径!  --- 重试第" + retry + "次");
            y = getThePath(x, pathNum, retry + 1);
        } else {
            pathList.removeLast();
            pathList.addAll(list);
            pathOdd -= list.size();
        }
        return y;
    }

    /**
     * 初始化起点与终点
     */
    private void initOriginAndDestination() {
        switch (orientation) {
            case 0:
                origin = new Point(0, 0);
                break;
            case 1:
                origin = new Point(mapWidth() - 1, 0);
                break;
            case 2:
                origin = new Point(mapWidth() - 1, mapHeigh() - 1);
                break;
            case 3:
                origin = new Point(0, mapHeigh() - 1);
                break;
            default:
                break;
        }
        mapArray[origin.getX()][origin.getY()] = 1;
    }

    /**
     * 初始化寻径算法重心偏向  0-代表左上角即以右下为主  1-代表右上角即以左下为主
     * // 2-代表右下角即以左上为主 3-代表左下角即以右上为主
     */
    private AStar initAStar() {
        int left = random.nextInt(6);
        int right = random.nextInt(6);
        int up = random.nextInt(6);
        int down = random.nextInt(6);
        return new AStar(mapArray, mapWidth(), mapHeigh(), left, right, up, down);
    }

    /**
     * 随机选取下一个节点
     *
     * @param curPoint 当前节点
     * @return Point 下一节点对象
     */
    private Point getNextPoint(Point curPoint) {
        int offsetY = random.nextInt(4) - 1;
        int offsetX = random.nextInt(mapWidth() / 2);
        int nextX = curPoint.getX() <= mapWidth() / 2 ? mapWidth() / 2 + offsetX - 1 : offsetX + 1;
        int nextY = 0;
        switch (orientation) {
            case 0:
            case 1:
                nextY = curPoint.getY() + offsetY < 0 ? 0 : curPoint.getY() + offsetY > mapHeigh() - 1 ?
                        mapHeigh() - 1 : curPoint.getY() + offsetY;
                if (nextY == mapHeigh() - 1) {
                    // 如果到最后一行的时候，结束后续寻径
                    pathOdd = 0;
                }
                break;
            case 2:
            case 3:
                nextY = curPoint.getY() - offsetY > mapHeigh() - 1 ? mapHeigh() - 1 : curPoint.getY() - offsetY < 0 ?
                        0 : curPoint.getY() - offsetY;
                if (nextY == 0) {
                    // 如果到第一行的时候，结束后续寻径
                    pathOdd = 0;
                }
                break;
            default:
                break;
        }
        if (mapArray[nextX][nextY] == 0) {
            return new Point(nextX, nextY);
        } else {
            return getNextPoint(curPoint);
        }
    }

    public int[][] getMapArray() {
        return mapArray;
    }

    public LinkedList<PathPoint> getPathList() {
        return pathList;
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
        while (!map.init()) {
            map = new MadeInArray();
        }
        int[][] array = map.getMapArray();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j]);
            }
            System.out.println();
        }
        Object[] ps = map.getPathList().toArray();
        for (int i = 0; i < ps.length; i++) {
            System.out.println(ps[i].toString() + " -->> " + i);
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