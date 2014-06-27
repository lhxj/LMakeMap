package com.example.makemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2014/6/16.
 */
public class AStar {
    private int[][] map;//地图(1可通过 0不可通过)
    private List<Node> openList;//开启列表
    private List<Node> closeList;//关闭列表
    //4个方向上的权重有一定的规则，默认无重心
    private static final int DEFAULT_STRAIGHT = 10;
    private int straightLeft;
    private int straightRight;
    private int straightUp;
    private int straightDown;
    private int row;//行
    private int column;//列

    public AStar(int[][] map, int row, int column) {
        this.map = map;
        this.row = row;
        this.column = column;
        openList = new ArrayList<Node>();
        closeList = new ArrayList<Node>();
        straightDown = straightLeft = straightRight = straightUp = DEFAULT_STRAIGHT;
    }

    public AStar(int[][] map, int row, int column, int straightLeft, int straightRight, int straightUp, int straightDown) {
        this.map = map;
        this.row = row;
        this.column = column;
        openList = new ArrayList<Node>();
        closeList = new ArrayList<Node>();
        this.straightLeft = DEFAULT_STRAIGHT + straightLeft;
        this.straightRight = DEFAULT_STRAIGHT + straightRight;
        this.straightUp = DEFAULT_STRAIGHT + straightUp;
        this.straightDown = DEFAULT_STRAIGHT + straightDown;
    }

    //查找坐标（-1：错误，0：没找到，1：找到了）
    public List<PathPoint> search(int x1, int y1, int x2, int y2, int count) {
        if (x1 < 0 || x1 >= row || x2 < 0 || x2 >= row || y1 < 0 || y1 >= column || y2 < 0 || y2 >= column) {
            return null;
        }
        if (map[x1][y1] != 1) {
            return null;
        }
        Node sNode = new Node(x1, y1, null);
        Node eNode = new Node(x2, y2, null);
        openList.add(sNode);
        List<Node> resultList = search(sNode, eNode);
        List<PathPoint> pathList = new ArrayList<PathPoint>();
        for (Node node : resultList) {
            pathList.add(new PathPoint(node.getX(), node.getY()));
            map[node.getX()][node.getY()] = count;
        }
        return pathList;
    }

    //查找核心算法
    private List<Node> search(Node sNode, Node eNode) {
        List<Node> resultList = new ArrayList<Node>();
        boolean isFind = false;
        Node node = null;
        while (openList.size() > 0) {
            //取出开启列表中最低F值，即第一个存储的值的F为最低的
            node = openList.get(0);
            //判断是否找到目标点
            if (node.getX() == eNode.getX() && node.getY() == eNode.getY()) {
                isFind = true;
                break;
            }
            //上
            if ((node.getY() - 1) >= 0) {
                checkPath(node.getX(), node.getY() - 1, node, eNode, straightUp);
            }
            //下
            if ((node.getY() + 1) < column) {
                checkPath(node.getX(), node.getY() + 1, node, eNode, straightDown);
            }
            //左
            if ((node.getX() - 1) >= 0) {
                checkPath(node.getX() - 1, node.getY(), node, eNode, straightLeft);
            }
            //右
            if ((node.getX() + 1) < row) {
                checkPath(node.getX() + 1, node.getY(), node, eNode, straightRight);
            }
            //从开启列表中删除
            //添加到关闭列表中
            closeList.add(openList.remove(0));
            //开启列表中排序，把F值最低的放到最底端
            Collections.sort(openList, new NodeFComparator());
        }
        if (isFind) {
            getPath(resultList, node);
        }
        return resultList;
    }

    //查询此路是否能走通
    private boolean checkPath(int x, int y, Node parentNode, Node eNode, int cost) {
        Node node = new Node(x, y, parentNode);
        //查找地图中是否能通过
        if (map[x][y] != 0) {
            closeList.add(node);
            return false;
        }
        //查找关闭列表中是否存在
        if (isListContains(closeList, x, y) != -1) {
            return false;
        }
        //查找开启列表中是否存在
        int index = -1;
        if ((index = isListContains(openList, x, y)) != -1) {
            //G值是否更小，即是否更新G，F值
            if ((parentNode.getG() + cost) < openList.get(index).getG()) {
                node.setParentNode(parentNode);
                countG(node, eNode, cost);
                countF(node);

                openList.set(index, node);
            }
        } else {
            //添加到开启列表中
            node.setParentNode(parentNode);
            count(node, eNode, cost);
            openList.add(node);
        }
        return true;
    }

    //集合中是否包含某个元素(-1：没有找到，否则返回所在的索引)
    private int isListContains(List<Node> list, int x, int y) {
        for (int i = 0; i < list.size(); i++) {
            Node node = list.get(i);
            if (node.getX() == x && node.getY() == y) {
                return i;
            }
        }
        return -1;
    }

    //从终点往返回到起点
    private void getPath(List<Node> resultList, Node node) {
        if (node.getParentNode() != null) {
            getPath(resultList, node.getParentNode());
        }
        resultList.add(node);
    }

    //计算G,H,F值
    private void count(Node node, Node eNode, int cost) {
        countG(node, eNode, cost);
        countH(node, eNode);
        countF(node);
    }

    //计算G值
    private void countG(Node node, Node eNode, int cost) {
        if (node.getParentNode() == null) {
            node.setG(cost);
        } else {
            node.setG(node.getParentNode().getG() + cost);
        }
    }

    //计算H值
    private void countH(Node node, Node eNode) {
        node.setF((Math.abs(node.getX() - eNode.getX()) + Math.abs(node.getY() - eNode.getY())) * 10);
    }

    //计算F值
    private void countF(Node node) {
        node.setF(node.getG() + node.getH());
    }
}

//节点类
class Node {
    private int x;//X坐标
    private int y;//Y坐标
    private Node parentNode;//父类节点
    private int g;//当前点到起点的移动耗费
    private int h;//当前点到终点的移动耗费，即曼哈顿距离|x1-x2|+|y1-y2|(忽略障碍物)
    private int f;//f=g+h

    public Node(int x, int y, Node parentNode) {
        this.x = x;
        this.y = y;
        this.parentNode = parentNode;
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

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public String toString() {
        return "(" + x + "," + y + "," + f + ")";
    }
}

//节点比较类
class NodeFComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return o1.getF() - o2.getF();
    }

}
