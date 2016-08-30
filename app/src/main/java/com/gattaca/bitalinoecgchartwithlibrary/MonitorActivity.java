package com.gattaca.bitalinoecgchartwithlibrary;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gattaca.bitalinoecgchartwithlibrary.navigation.NaviagableActivity;

public class MonitorActivity extends NaviagableActivity {

    RealTimeChart staticChart;
    BitalinoUniversal bitalino = null;
    Thread startBitalinoThread = null;
    static final int PERIOD = 100;
    static final String TAG = MonitorActivity.class.getSimpleName();
    boolean fab_state;
    ChartAdapter staticChartAdapter;
    public EventsAdapter eventsAdapter;
    LinearLayout EventsLayout;
    LinearLayout.LayoutParams layoutParams;
    FloatingActionButton fab;
    SinDevice sinDevice;
    BitalinoDevice bitalinoDevice;
    RealTimeChart dynamicChart;
    ChartsManager chartsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_monitor, frameLayout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_monitor);

        EventsLayout = (LinearLayout) findViewById(R.id.root_layout);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 20, 30, 0);

        //toolbar.setTitleTextColor(getColor(R.color.colorPrimary));
        //toolbar.setSubtitleTextColor(getColor(R.color.colorPrimary));
        //toolbar.setBackgroundColor(getColor(R.color.colorPrimary));

        setSupportActionBar(toolbar);
        onCreateToolbar(toolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        staticChart = new RealTimeChart(this, false);
        dynamicChart = new RealTimeChart(this, true);

        eventsAdapter = new EventsAdapter(this);

        staticChart.init(false);
        dynamicChart.init(true);

        bitalino = new BitalinoUniversal(this, 0);
        //startBitalinoThread = bitalino.start();

        staticChartAdapter = new ChartAdapter(staticChart, this);

        chartsManager = new ChartsManager(staticChart, dynamicChart, staticChartAdapter);
        sinDevice = new SinDevice(100, staticChartAdapter);
        //bitalinoDevice = new BitalinoDevice(bitalino.SAMPLE_RATE, staticChartAdapter, bitalino);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;

        fab_state = true;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fab_state) {
                    staticChartAdapter.moveRealTime();
                    fab.setImageDrawable(getDrawable(R.drawable.pause));
                } else {
                    staticChartAdapter.pause();
                    fab.setImageDrawable(getDrawable(R.drawable.play));
                }
                fab_state = !fab_state;
            }
        });

        staticChartAdapter.receive(0f);

        staticChartAdapter.start();

        /*Thread realTimeThread = new Thread(new Runnable() {
            SimpleECG currentECG = null;
            int i;

            @Override
            public void run() {
                Log.e(TAG, "I am in realTime.");
                try {
                    startBitalinoThread.join();
                } catch (InterruptedException e) {}
                Log.e(TAG, "Now we ready to go!");

                if (bitalino.isConnected.get()) {
                    while (true) {
                        currentECG = bitalino.get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                staticChart.addData((float) currentECG.get());
                            }
                        });
                    }
                } else {
                    for (i = 30; ; i = (i + 1) % PERIOD) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                staticChart.addData((float)Math.sin(2.0 * Math.PI * i / PERIOD));
                            }
                         });
                        long waitUntil = System.nanoTime() + 16666666;
                        while(waitUntil > System.nanoTime());
                        //catch (InterruptedException e) {}
                    }
                }
            }
        });
        realTimeThread.start();*/
    }

    void addButton(Float position) {
        Button button = (Button)getLayoutInflater().inflate(R.layout.monitor_list_element, null);
        button.setText(Float.toString(position));
        button.setId(eventsAdapter.getEventsSize() - 1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staticChartAdapter.move(eventsAdapter.get_event(view.getId()) - staticChart.VISIBLE_NUM / 2);
                chartsManager.setState(chartsManager.SCROLL);
                fab_state = false;
                fab.setImageDrawable(getDrawable(R.drawable.play));
            }
        });

        EventsLayout.addView(button, layoutParams);
    }

    @Override
    public void onDestroy() {
        staticChartAdapter.close();
        if (sinDevice != null)
            sinDevice.close();
        if (bitalino != null)
            bitalino.close();
        if (bitalinoDevice != null)
            bitalinoDevice.close();
        super.onDestroy();
    }
}

