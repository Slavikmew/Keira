package com.gattaca.bitalinoecgchartwithlibrary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vadub on 18.08.2016.
 */

public class SinDevice {
    int frequency;
    public static final int period = 100;

    private Thread collectingDataThread;
    private ChartAdapter chartAdapter;

    SinDevice(int initFrequency, ChartAdapter initChartAdapter) {
        chartAdapter = initChartAdapter;
        frequency = initFrequency;
        collectingDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentAlpha = 0;
                while(true) {
                    chartAdapter.push_back((float)Math.sin(2 * Math.PI * currentAlpha / period));
                    currentAlpha++;
                    currentAlpha %= period;
                    EffectiveSleep.sleepNanoseconds(1000000000 / frequency);
                }
            }
        });
        collectingDataThread.start();
    }

}