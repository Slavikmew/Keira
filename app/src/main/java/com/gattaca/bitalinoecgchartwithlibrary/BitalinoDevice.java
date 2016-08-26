package com.gattaca.bitalinoecgchartwithlibrary;

import android.util.Log;

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
                        chartAdapter.push_back((float) bitalino.get().get());
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
