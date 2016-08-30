package com.gattaca.bitalinoecgchartwithlibrary;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by vadub on 26.08.2016.
 */
public class BitalinoDevice {
    int frequency;

    private Thread collectingDataThread;
    private ChartAdapter chartAdapter;
    private BitalinoUniversal bitalino;
    Thread startBitalinoThread;

    BitalinoDevice(int initFrequency, ChartAdapter initChartAdapter, BitalinoUniversal initBitalino) {
        frequency = initFrequency;
        chartAdapter = initChartAdapter;
        bitalino = initBitalino;
        startBitalinoThread = bitalino.start();

        collectingDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentAlpha = 0;
                try {
                    startBitalinoThread.join();
                } catch (InterruptedException e) {}
                if (bitalino.isConnected.get()) {
                    while (true) {
                        try {
                            long time = System.nanoTime();
                            chartAdapter.updateData.put((float) bitalino.get().get());
                            Log.i(BitalinoDevice.class.getSimpleName(), String.valueOf(System.nanoTime() - time));

                        } catch (InterruptedException e) {}
                    }
                } else {
                    Log.i(BitalinoDevice.class.getSimpleName(), "Cant connect to Bitalino!!");
                }
            }
        });
        collectingDataThread.start();
    }

    public void close() {
        collectingDataThread.interrupt();
    }
}
