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
    private DataReceiver chartAdapter;

    SinDevice(int initFrequency, DataReceiver initDataReceiver) {
        chartAdapter = initDataReceiver;
        frequency = initFrequency;
        collectingDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentAlpha = 0;
                while(true) {
                    chartAdapter.receive((float)Math.sin(2 * Math.PI * currentAlpha / period));
                    currentAlpha++;
                    currentAlpha %= period;
                    EffectiveSleep.sleepNanoseconds(1000000000 / frequency);
                }
            }
        });
        collectingDataThread.start();
    }

    public void close() {
        collectingDataThread.interrupt();
    }
}