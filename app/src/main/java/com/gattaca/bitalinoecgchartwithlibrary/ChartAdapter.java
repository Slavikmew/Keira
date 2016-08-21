package com.gattaca.bitalinoecgchartwithlibrary;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by vadub on 18.08.2016.
 */
public class ChartAdapter {
    private RealTimeChart chart;
    public static final int FPS = 60;
    Thread changeChartThread;
    Device device;
    Activity UIThreadActivity;
    private boolean isRealTime = true;
    private float moveXto;
    boolean alreadyMove;

    synchronized public boolean getRealTime() {
        return isRealTime;
    }
    synchronized public void setRealTime(boolean value) {
        isRealTime = value;
    }
    synchronized public boolean getAlreadyMove() {
        return alreadyMove;
    }
    synchronized public void setAlreadyMove(boolean value) {
        alreadyMove = value;
    }
    synchronized public float getMoveXto() {
        return moveXto;
    }
    synchronized public void setMoveXto(float initMoveXto) {
        moveXto = initMoveXto;
    }

    ChartAdapter(RealTimeChart realTimeChart, Device d, Activity activity) {
        chart = realTimeChart;
        device = d;
        UIThreadActivity = activity;
        moveXto = Float.MAX_VALUE;
    }

    class UpdateRunnable implements Runnable {
        ArrayList<Float> data;
        float moveXto;

        UpdateRunnable(ArrayList<Float> currentData, float MoveXto) {
            data = currentData;
            moveXto = MoveXto;
        }

        @Override
        public void run() {
            chart.addData(data, getRealTime(), moveXto);
        }
    }

    private class MoveTask implements Runnable {
        float startPosition, finalPosition;
        static final int scrollFrequency = 1000;

        MoveTask(float startPosition, float position) {
            finalPosition = position;
        }

        @Override
        public void run() {
            setAlreadyMove(true);
            float currentPosition = startPosition;
            while(true) {
                if (Math.abs(currentPosition - Math.min(finalPosition, chart.size())) < 1e-9) {
                    break;
                }
                if (currentPosition < finalPosition) {
                    currentPosition++;
                } else {
                    currentPosition--;
                }
                setMoveXto(currentPosition);
                long waitUntil = System.nanoTime() + 1000000000 / scrollFrequency;
                while(waitUntil > System.nanoTime());
            }
            setAlreadyMove(false);
            if (finalPosition == Float.MAX_VALUE) {
                setRealTime(true);
            }
        }
    }

    public boolean move(float finalX) {
        if (getAlreadyMove())
            return false;
        new Thread(new MoveTask(chart.getViewX(), finalX)).start();
        return true;
    }

    public boolean moveRealTime() {
        return move(Float.MAX_VALUE);
    }

    public void start() {
        changeChartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int cur = 0;
                while(true) {
                    ArrayList<Float> currentData = device.get();
                    UpdateRunnable currentRunnable = new UpdateRunnable(currentData, moveXto);
                    UIThreadActivity.runOnUiThread(currentRunnable);
                    long waitUntil = System.nanoTime() + 1000000000 / FPS;
                    while(waitUntil > System.nanoTime());
                }
            }
        });
        changeChartThread.start();
    }
}
