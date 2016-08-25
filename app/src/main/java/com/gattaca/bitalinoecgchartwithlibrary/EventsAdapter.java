package com.gattaca.bitalinoecgchartwithlibrary;

import java.util.ArrayList;

/**
 * Created by vadub on 25.08.2016.
 */
public class EventsAdapter {
    ArrayList<Float> events;
    MonitorActivity monitorActivity;

    EventsAdapter(MonitorActivity initMonitorActivity) {
        monitorActivity = initMonitorActivity;
        events = new ArrayList<Float>();
    }

    synchronized public float get_event(int i) {
        return events.get(i);
    }

    synchronized public void add_event(float xValue) {
        events.add(xValue);
        monitorActivity.addButton(xValue);
    }

    synchronized public int getEventsSize() {
        return events.size();
    }
}
