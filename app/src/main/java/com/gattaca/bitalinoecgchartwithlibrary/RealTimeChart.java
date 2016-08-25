package com.gattaca.bitalinoecgchartwithlibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;

public class RealTimeChart  implements OnChartGestureListener {

    private LineChart mChart;
    MonitorActivity mActivity;
    static final float CHART_WIDTH = 4;
    public static final int VISIBLE_NUM = 300;

    public RealTimeChart(MonitorActivity monitorActivity) {
        mActivity = monitorActivity;
    }

    public void init() {

        mChart = (LineChart)mActivity.findViewById(R.id.chart);

        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        mChart.setTouchEnabled(true);
        mChart.setOnChartGestureListener(this);

        /*mChart.setDrawBorders(true);
        mChart.setBorderColor(Color.GREEN);
        mChart.setBorderWidth(CHART_WIDTH);*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        //mChart.setDrawGridBackground(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setGridBackgroundColor(Color.WHITE);

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        //mChart.getXAxis().setDrawGridLines(true);

        //mChart.setVisibleXRange(VISIBLE_NUM, VISIBLE_NUM);

        mChart.setPinchZoom(true);

        mChart.setBackgroundColor(Color.WHITE);

       /*Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.net_upper_gradient);
        Drawable background = new BitmapDrawable(mActivity.getResources(), bm);
        mChart.setBackground(background);*/

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        //Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        //l.setForm(Legend.LegendForm.LINE);

        //l.setTypeface(mTfLight);
        //l.setTextColor(Color.BLACK);

        /*XAxis xl = mChart.getXAxis();
        //xl.setTypeface(mTfLight);
        xl.setTextColor(Color.BLACK);
        //xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(1.5f);
        leftAxis.setAxisMinValue(-1.5f);
        //leftAxis.setDrawGridLines(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);*/
        //mChart.setEnabled(false);
        data.addDataSet(createSet());
        data.addDataSet(createEventSet());
    }

    synchronized public void addData(float value, boolean isRealTime) {
        LineData data = mChart.getData();
        LineDataSet set = (LineDataSet)data.getDataSetByIndex(0);

        /*if(set.getEntryCount() == VISIBLE_NUM) {
            set.removeFirst();

            for (Entry entry : set.getValues()) {
                entry.setX(entry.getX() - 1);
            }
        }*/

        data.addEntry(new Entry(set.getEntryCount(), value), 0);

        if (!isRealTime) {
            mChart.moveViewToX(set.getEntryCount() - VISIBLE_NUM);
        }
        mChart.setVisibleXRange(VISIBLE_NUM, VISIBLE_NUM);
        mChart.notifyDataSetChanged();
        mChart.postInvalidate();
    }

    synchronized public void addData(ArrayList<Float> values, float moveXto, boolean isEvent) {
        LineData data = mChart.getData();
        LineDataSet set = (LineDataSet)data.getDataSetByIndex(0);

        /*if(set.getEntryCount() == VISIBLE_NUM) {
            set.removeFirst();

            for (Entry entry : set.getValues()) {
                entry.setX(entry.getX() - 1);
            }
        }*/

        for (float value: values) {
            data.addEntry(new Entry(set.getEntryCount(), value), 0);
        }

        if (isEvent) {
            data.addEntry(new Entry(set.getEntryCount(), values.get(0)), 1);
        }

        mChart.moveViewToX(moveXto);

        mChart.setVisibleXRange(VISIBLE_NUM, VISIBLE_NUM);
        mChart.notifyDataSetChanged();
        //mChart.postInvalidate();
    }

    synchronized int size() {
        return mChart.getLineData().getDataSetByIndex(0).getEntryCount();
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "y = sin(x)");
        //set.enableDashedLine(10f, 5f, 0f);
        set.setColor(Color.BLACK);
        //set.setCircleColor(Color.RED);
        set.setDrawCircles(false);
        set.setLineWidth(CHART_WIDTH);
        //set.setDrawCircleHole(true);
        set.setValueTextSize(12f);
        //set.setFillAlpha(65);
        //set.setFillColor(Color.GREEN);
        return set;
    }

    private LineDataSet createEventSet() {
        LineDataSet set = new LineDataSet(null, "y = sin(x)");
        //set.enableDashedLine(10f, 5f, 0f);
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.RED);
        set.setDrawCircles(true);
        set.setCircleRadius(10f);
        set.setDrawFilled(false);
        //set.setLineWidth(CHART_WIDTH);
        set.setDrawCircleHole(true);
        //set.setValueTextSize(12f);
        //set.setFillAlpha(65);
        //set.setFillColor(Color.GREEN);
        return set;
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
    @Override
    public void onChartLongPressed(MotionEvent me) {}
    @Override
    public void onChartDoubleTapped(MotionEvent me) {}
    @Override
    public void onChartSingleTapped(MotionEvent me) {}

}
