package com.gattaca.bitalinoecgchartwithlibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gattaca.bitalinoecgchartwithlibrary.navigation.NaviagableActivity;

import java.util.ArrayList;

public class MonitorActivity extends NaviagableActivity {

    RealTimeChart chart;
    BitalinoUniversal bitalino = null;
    Thread startBitalinoThread = null;
    static final int PERIOD = 100;
    static final String TAG = MonitorActivity.class.getSimpleName();
    boolean fab_state;
    ChartAdapter currentChartAdapter;
    public EventsAdapter eventsAdapter;
    LinearLayout EventsLayout;
    LinearLayout.LayoutParams layoutParams;
    FloatingActionButton fab;

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

        chart = new RealTimeChart(this);
        eventsAdapter = new EventsAdapter(this);

        chart.init();


        /*bitalino = new BitalinoUniversal(this, 2);
        startBitalinoThread = bitalino.start();*/

        currentChartAdapter = new ChartAdapter(chart, this);
        SinDevice sinDevice = new SinDevice(100, currentChartAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;

        fab_state = true;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fab_state) {
                    currentChartAdapter.moveRealTime();
                    fab.setImageDrawable(getDrawable(R.drawable.pause));
                } else {
                    currentChartAdapter.pause();
                    fab.setImageDrawable(getDrawable(R.drawable.play));
                }
                fab_state = !fab_state;
            }
        });

        currentChartAdapter.start();

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
                                chart.addData((float) currentECG.get());
                            }
                        });
                    }
                } else {
                    for (i = 30; ; i = (i + 1) % PERIOD) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chart.addData((float)Math.sin(2.0 * Math.PI * i / PERIOD));
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
                currentChartAdapter.move(eventsAdapter.get_event(view.getId()) - chart.VISIBLE_NUM / 2);
                fab_state = false;
                fab.setImageDrawable(getDrawable(R.drawable.play));
            }
        });

        EventsLayout.addView(button, layoutParams);
    }
}
