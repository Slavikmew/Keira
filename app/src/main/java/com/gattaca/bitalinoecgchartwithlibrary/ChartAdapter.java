package com.gattaca.bitalinoecgchartwithlibrary;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by vadub on 18.08.2016.
 */
public class ChartAdapter implements DataReceiver {
    public static final int FPS = 60;

    private RealTimeChart staticChart, dynamicChart;
    Thread changeChartThread, moveThread;
    public BlockingQueue<Float> updateData;

    MoveManager moveManager;
    Random myRandom;
    MonitorActivity UIThreadActivity;

    ChartAdapter(RealTimeChart initStaticChart, RealTimeChart initDynamicChart, MonitorActivity activity) {
        staticChart = initStaticChart;
        dynamicChart = initDynamicChart;
        updateData = new LinkedBlockingQueue<Float>();
        UIThreadActivity = activity;
        moveManager = new MoveManager();
        myRandom = new Random(171717);
    }

    class UpdateRunnable implements Runnable {
        ArrayList<Float> data;
        float moveXto;

        UpdateRunnable(BlockingQueue<Float> queue, float MoveXto) {
            data = new ArrayList<Float>();
            while (!queue.isEmpty()) {
                try {
                    Float item = queue.take();
                    if (item != null) {
                        data.add(item);
                    }
                } catch (InterruptedException e) {}
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
            if (data.size() > 0)
                Log.i(ChartAdapter.class.getSimpleName(), String.valueOf(data.size()));
            synchronized (ChartAdapter.this) {
                staticChart.addData(new ArrayList<Float>(data), moveXto, isEvent);
                dynamicChart.updateData(new ArrayList<Float>(data), isEvent);
            }
        }
    }

    public void toDynamic() {
        dynamicChart.set_enable(true);
        staticChart.set_enable(false);
    }

    public void toStatic() {
        dynamicChart.set_enable(false);
        staticChart.set_enable(true);
    }
    synchronized public void receive(float value) {
        updateData.add(value);
    }

    int getChartSize() {
        return staticChart.size();
    }

    private class MoveManager implements Runnable {
        private float currentPosition, finalPosition, actualPosition;
        static final int MAXIMAL_SPEED = 10000;
        static final int MINIMUM_SPEED = 700;

        MoveManager() {
            currentPosition = actualPosition = getChartSize() - staticChart.VISIBLE_NUM;
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
                    if (Math.abs(currentPosition - Math.min(finalPosition, staticChart.size() - staticChart.VISIBLE_NUM)) > 1e-9) {
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
                    distance = (long) Math.abs(currentPosition - Math.min(finalPosition, staticChart.size() - staticChart.VISIBLE_NUM));
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
