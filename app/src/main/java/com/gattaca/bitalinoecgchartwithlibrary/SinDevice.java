package com.gattaca.bitalinoecgchartwithlibrary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vadub on 18.08.2016.
 */

public class SinDevice implements Device {
    int frequency;
    public static final int period = 100;

    private ArrayList<Float> points;
    private Thread collectingDataThread;

    synchronized private void push_back(Float value) {
        points.add(value);
    }

    SinDevice(int initFrequency) {
        frequency = initFrequency;
        points = new ArrayList<Float>();
        collectingDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentAlpha = 0;
                while(true) {
                    push_back((float)Math.sin(2 * Math.PI * currentAlpha / period));
                    currentAlpha++;
                    currentAlpha %= period;
                    long waitUntil = System.nanoTime() + 1000000000 / frequency;
                    while(waitUntil > System.nanoTime());
                }
            }
        });
        collectingDataThread.start();
    }

    synchronized public ArrayList<Float> get() {
        ArrayList<Float> returnValue = new ArrayList<Float>(points.size());
        for (Float value: points) {
            returnValue.add(value);
        }
        points.clear();
        /*Random random = new Random(1234);
        ArrayList<Float> cur = new ArrayList<Float>();
        cur.add((float)Math.sin(2 * Math.PI * System.currentTimeMillis() / 100));
        cur.add((float)Math.sin(2 * Math.PI * (System.currentTimeMillis() + 1) / 100));*/
        //cur.add(random.nextFloat());
        //return cur;
        return returnValue;
    }

}