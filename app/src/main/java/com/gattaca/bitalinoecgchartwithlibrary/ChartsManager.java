package com.gattaca.bitalinoecgchartwithlibrary;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vadub on 30.08.2016.
 */
public class ChartsManager implements DataReceiver {
    public static final int REAL_TIME = 0;
    public static final int PAUSE = 1;
    public static final int SCROLL = 2;
    RealTimeChart staticChart, dynamicChart;
    ChartAdapter staticChartAdapter;
    private AtomicInteger state;

    ChartsManager(RealTimeChart initStaticChart, RealTimeChart initDynamicChart, ChartAdapter initStaticChartAdapter) {
        staticChart = initStaticChart;
        dynamicChart = initDynamicChart;
        staticChartAdapter = initStaticChartAdapter;
        state = new AtomicInteger(0);
    }

    public int getState() {
        return state.get();
    }

    public void setState(int newState) {
        switch (newState) {
            case PAUSE:
                state.set(PAUSE);
                staticChart.set_enable(true);
                dynamicChart.set_enable(false);
                break;
            case SCROLL:
                state.set(SCROLL);
                staticChart.set_enable(true);
                dynamicChart.set_enable(false);
                break;
            case REAL_TIME:
                state.set(REAL_TIME);
                staticChart.set_enable(false);
                dynamicChart.set_enable(true);
                break;
        }
    }

    public void receive(float value) {
        staticChartAdapter.receive(value);
        ArrayList<Float> local = new ArrayList<Float>();
        local.add(value);
        dynamicChart.updateData(local, false); // TODO Разобраться с событиями
    }
}
