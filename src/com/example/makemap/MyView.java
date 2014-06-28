package com.example.makemap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;

/**
 * 界面
 * Created by Administrator on 2014/6/25.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "MyView";

    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private Resources resources;
    private Bitmap bitmap;
    private Bitmap renwu;
    private int screenWidth, screenHeight;
    //路径点情况记录
    private LinkedList<PathPoint> pathList;
    //地图参数数组结构
    private int[][] mapArray;
    //用于同步锁的字节码对象
    private byte[] aByte = new byte[0];
    private int times = 0;
    private Paint paint = new Paint();

    //地图一些测试参数
    private int[][] back = {{0, 1}};
    private int[][] path = {{1, 1}};
    private int[][] barr = {{0, 0}};

    public MyView(Context context) {
        super(context);
        Log.d(TAG, "构造函数开始---------------------");
        thread = new Thread(this);
        resources = this.getResources();
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.pic);
        renwu = BitmapFactory.decodeResource(resources, R.drawable.renwu);
        surfaceHolder = this.getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(this);
        }
        this.setKeepScreenOn(true);
        Log.d(TAG, "测试地图是否生成开始---------success");
        MadeInArray map = new MadeInArray();
        while (!map.init()) {
            map = new MadeInArray();
        }
        Log.d(TAG, "测试地图是否生成成功---------success");
        mapArray = map.getMapArray();
        pathList = map.getPathList();
    }

    private void onDraw() {
        //Log.d(TAG, "测试onDraw方法---------success");
        drawGirlRun(renwu.getWidth() / 8, renwu.getHeight() / 4, 0, times, paint);
        times += 1;
    }

    /**
     * 求相邻2点的相对位置
     *
     * @param reference 参照点
     * @param next      临近点
     * @return 0-左 1-右 2-上 3-下
     */
    private int relative(PathPoint reference, PathPoint next) {
        int relation;
        if (reference.getX() > next.getX()) {
            relation = 0;
        } else if (reference.getX() < next.getX()) {
            relation = 1;
        } else {
            if (reference.getY() > next.getY()) {
                relation = 2;
            } else {
                relation = 3;
            }
        }
        return relation;
    }

    private void initBackGround(int mapGridW, int mapGridH) {
        int mW = mapArray.length;
        int mH = mapArray[0].length;
        int gridWidth = screenWidth / mW;
        int widthOffset = screenWidth % mW;
        int gridHeight = screenHeight / mH;
        int heightOffset = screenHeight % mH;
        Paint p = new Paint();
        Bitmap back = Bitmap.createBitmap(bitmap, mapGridW, 0, mapGridW, mapGridH);
        Bitmap pathX = Bitmap.createBitmap(bitmap, mapGridW, mapGridH, mapGridW, mapGridH);
        Bitmap pathY = Bitmap.createBitmap(bitmap, 0, 2 * mapGridH, mapGridW, mapGridH);
        Bitmap pathLU = Bitmap.createBitmap(bitmap, 0, mapGridH, mapGridW, mapGridH);
        Bitmap pathRU = Bitmap.createBitmap(bitmap, 3 * mapGridW, mapGridH, mapGridW, mapGridH);
        Bitmap pathRD = Bitmap.createBitmap(bitmap, 3 * mapGridW, 2 * mapGridH, mapGridW, mapGridH);
        Bitmap pathLD = Bitmap.createBitmap(bitmap, 2 * mapGridW, 2 * mapGridH, mapGridW, mapGridH);
        Bitmap pathDE = Bitmap.createBitmap(bitmap, 5 * mapGridW, mapGridH, mapGridW, mapGridH);
        Bitmap barr = Bitmap.createBitmap(bitmap, 0, 0, mapGridW, mapGridH);
        Matrix matrix = new Matrix();
        Log.d(TAG, "-----测试数据： gridw = " + gridWidth + " -- mapw = " + bitmap.getWidth() + " -- gridh = " + gridHeight + " -- maph = " + bitmap.getHeight());
        matrix.postScale(((float) gridWidth) / mapGridW, ((float) gridHeight) / mapGridH);
        back = Bitmap.createBitmap(back, 0, 0, mapGridW, mapGridH, matrix, true);
        pathX = Bitmap.createBitmap(pathX, 0, 0, mapGridW, mapGridH, matrix, true);
        pathY = Bitmap.createBitmap(pathY, 0, 0, mapGridW, mapGridH, matrix, true);
        pathLU = Bitmap.createBitmap(pathLU, 0, 0, mapGridW, mapGridH, matrix, true);
        pathRU = Bitmap.createBitmap(pathRU, 0, 0, mapGridW, mapGridH, matrix, true);
        pathRD = Bitmap.createBitmap(pathRD, 0, 0, mapGridW, mapGridH, matrix, true);
        pathLD = Bitmap.createBitmap(pathLD, 0, 0, mapGridW, mapGridH, matrix, true);
        pathDE = Bitmap.createBitmap(pathDE, 0, 0, mapGridW, mapGridH, matrix, true);
        barr = Bitmap.createBitmap(barr, 0, 0, mapGridW, mapGridH, matrix, true);
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screenWidth, screenHeight));
            if (canvas != null) {
                canvas.save();
                for (int i = 0; i < mW; i++) {
                    for (int j = 0; j < mH; j++) {
                        if (mapArray[i][j] == 0) {
                            canvas.drawBitmap(back, widthOffset + i * gridWidth, heightOffset + j * gridHeight, p);
                        } else if (mapArray[i][j] == 1) {
                        } else {
                            canvas.drawBitmap(barr, widthOffset + i * gridWidth, heightOffset + j * gridHeight, p);
                        }
                    }
                }
                for (int i = 0; i < pathList.size(); i++) {
                    PathPoint pathPoint = pathList.get(i);
                    if (i == 0 || i == pathList.size() - 1) {
                        canvas.drawBitmap(back, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        canvas.drawBitmap(pathDE, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                    } else {
                        PathPoint beforP = pathList.get(i - 1);
                        PathPoint afterP = pathList.get(i + 1);
                        if (relative(pathPoint, beforP) == 0 && relative(pathPoint, afterP) == 1) { //左右
                            canvas.drawBitmap(pathX, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 0 && relative(pathPoint, afterP) == 2) {//左上
                            canvas.drawBitmap(pathRD, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 0 && relative(pathPoint, afterP) == 3) {//左下
                            canvas.drawBitmap(pathRU, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 1 && relative(pathPoint, afterP) == 0) {//右左
                            canvas.drawBitmap(pathX, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 1 && relative(pathPoint, afterP) == 2) {//右上
                            canvas.drawBitmap(pathLD, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 1 && relative(pathPoint, afterP) == 3) {//右下
                            canvas.drawBitmap(pathLU, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 2 && relative(pathPoint, afterP) == 0) {//上左
                            canvas.drawBitmap(pathRD, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 2 && relative(pathPoint, afterP) == 1) {//上右
                            canvas.drawBitmap(pathLD, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 2 && relative(pathPoint, afterP) == 3) {//上下
                            canvas.drawBitmap(pathY, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 3 && relative(pathPoint, afterP) == 0) {//下左
                            canvas.drawBitmap(pathRU, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 3 && relative(pathPoint, afterP) == 1) {//下右
                            canvas.drawBitmap(pathLU, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        } else if (relative(pathPoint, beforP) == 3 && relative(pathPoint, afterP) == 2) {//下上
                            canvas.drawBitmap(pathY, widthOffset + pathPoint.getX() * gridWidth, heightOffset + pathPoint.getY() * gridHeight, p);
                        }
                    }
                }
                canvas.restore();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawGirlRun(int w, int h, int direction, int count, Paint paint) {
        Bitmap girl = Bitmap.createBitmap(renwu, w * (count % 8), h * direction, w, h);
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(new Rect(0, 0, w, h));
            /*if (canvas != null) {
                canvas.save();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawBitmap(girl, 0, 0, null);
                canvas.restore();
            }*/
        } catch (Exception e) {
            Log.e(TAG, "人物行走测试出现问题---" + e.getMessage());
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void run() {
        initBackGround(bitmap.getWidth() / 8, bitmap.getHeight() / 20);
        while (true) {
            onDraw();
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                Log.e(TAG, "run方法异常" + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = this.getWidth();
        screenHeight = this.getHeight();
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
