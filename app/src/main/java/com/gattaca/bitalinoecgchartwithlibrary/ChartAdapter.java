package com.gattaca.bitalinoecgchartwithlibrary;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by vadub on 18.08.2016.
 */
public class ChartAdapter {
    private RealTimeChart chart;
    public static final int FPS = 60;
    Thread changeChartThread, moveThread;
    MonitorActivity UIThreadActivity;
    ArrayList<Float> updateData;
    MoveManager moveManager;
    Random myRandom;

    ChartAdapter(RealTimeChart realTimeChart, MonitorActivity activity) {
        chart = realTimeChart;
        updateData = new ArrayList<Float>();
        UIThreadActivity = activity;
        moveManager = new MoveManager();
        myRandom = new Random(171717);
    }

    class UpdateRunnable implements Runnable {
        ArrayList<Float> data;
        float moveXto;

        UpdateRunnable(ArrayList<Float> currentData, float MoveXto) {
            data = new ArrayList<Float>();
            for (Float item: currentData) {
                if (item != null) {
                    data.add(item);
                }
            }
            moveXto = MoveXto;
        }

        @Override
        public void run() {
            boolean isEvent = false;
            if (data.size() > 0 && myRandom.nextInt(500) == 0) {
                UIThreadActivity.eventsAdapter.add_event(getChartSize());
                Log.i(ChartAdapter.class.getSimpleName(), Float.toString(data.get(0)));
                isEvent = true;
            }
            chart.addData(new ArrayList<Float>(data), moveXto, isEvent);
        }
    }

    synchronized public void push_back(Float value) {
        updateData.add(value);
    }

    int getChartSize() {
        return chart.size();
    }

    private class MoveManager implements Runnable {
        private float currentPosition, finalPosition, actualPosition;
        static final int MAXIMAL_SPEED = 10000;
        static final int MINIMUM_SPEED = 200;

        MoveManager() {
            currentPosition = actualPosition = getChartSize() - chart.VISIBLE_NUM;
            finalPosition = Float.MAX_VALUE;
        }

        synchronized void setCurrentPosition(Float initCurrentPosition) {
            currentPosition = initCurrentPosition;
        }

        synchronized void setFinalPosition(Float initFinalPosition) {
            finalPosition = initFinalPosition;
        }

        synchronized void setActualPosition(Float initActualPosition) {
            actualPosition = initActualPosition;
        }

        synchronized Float getCurrentPosition() {
            return currentPosition;
        }

        synchronized Float getFinalPosition() {
            return finalPosition;
        }

        synchronized Float getActualPosition() {
            return actualPosition;
        }


        @Override
        public void run() {
            while(true) {
                long distance = 0;
                synchronized (this) {
                    if (Math.abs(currentPosition - Math.min(finalPosition, chart.size() - chart.VISIBLE_NUM)) > 1e-9) {
                        if (currentPosition < Math.min(finalPosition, actualPosition)) {
                            currentPosition = Math.min(finalPosition, actualPosition);
                        }
                        if (currentPosition > Math.max(finalPosition, actualPosition)) {
                            currentPosition = Math.max(finalPosition, actualPosition);
                        }
                        if (currentPosition < finalPosition) {
                            currentPosition++;
                        } else {
                            currentPosition--;
                        }
                    }
                    distance = (long) Math.abs(currentPosition - Math.min(finalPosition, chart.size() - chart.VISIBLE_NUM));
                }
                EffectiveSleep.sleepNanoseconds(1000000000 / Math.max(MINIMUM_SPEED, Math.min(MAXIMAL_SPEED, distance)));
            }
        }
    }

    public void move(float finalX) {
        moveManager.setFinalPosition(finalX);
    }

    public void pause() {
        move(moveManager.getActualPosition());
    }

    public void moveRealTime() {
        move(Float.MAX_VALUE);
    }

    class FPSChanger implements Runnable {
        @Override
        public void run() {
            while(true) {
                synchronized (ChartAdapter.this) {
                    float newView = moveManager.getCurrentPosition();
                    UpdateRunnable currentRunnable = new UpdateRunnable(updateData, newView);
                    UIThreadActivity.runOnUiThread(currentRunnable);
                    updateData.clear();
                    moveManager.setActualPosition(newView);
                }
                EffectiveSleep.sleepNanoseconds(1000000000 / FPS);
            }
        }
    }

    public void start() {
        moveThread = new Thread(moveManager);
        changeChartThread = new Thread(new FPSChanger());
        changeChartThread.start();
        moveThread.start();
    }

    public void close() {
        changeChartThread.interrupt();
        moveThread.interrupt();
    }
}
