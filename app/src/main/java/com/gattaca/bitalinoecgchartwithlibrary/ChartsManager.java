package com.gattaca.bitalinoecgchartwithlibrary;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vadub on 30.08.2016.
 */
public class ChartsManager {
    public static final int REAL_TIME = 0;
    public static final int PAUSE = 1;
    public static final int SCROLL = 2;
    ChartAdapter chartAdapter;
    ViewSwitcher chartSwitcher;
    private AtomicInteger state;
    MonitorActivity monitorActivity;

    ChartsManager(MonitorActivity initMonitorActivity, ChartAdapter initChartAdapter, ViewSwitcher initChartSwitcher) {
        monitorActivity = initMonitorActivity;
        chartAdapter = initChartAdapter;
        chartSwitcher = initChartSwitcher;

        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(400);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(400);

        chartSwitcher.setInAnimation(inAnim);
        chartSwitcher.setOutAnimation(outAnim);

        state = new AtomicInteger(0);

        chartSwitcher.showNext();
    }

    public int getState() {
        return state.get();
    }

    public void setState(int newState) {
        switch (newState) {
            case PAUSE:
                if (getState() == REAL_TIME) {
                    monitorActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chartSwitcher.showNext();
                        }
                    });
                }
                state.set(PAUSE);
                chartAdapter.toStatic();
                break;
            case SCROLL:
                if (getState() == REAL_TIME) {
                    monitorActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chartSwitcher.showNext();
                        }
                    });
                }
                state.set(SCROLL);
                chartAdapter.toStatic();
                break;
            case REAL_TIME:
                if (getState() != REAL_TIME) {
                    monitorActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chartSwitcher.showNext();
                        }
                    });
                }
                state.set(REAL_TIME);
                chartAdapter.toDynamic();
                break;
        }
    }


}
