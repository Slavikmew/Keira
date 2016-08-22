package com.gattaca.bitalinoecgchartwithlibrary;

import java.util.concurrent.TimeUnit;

/**
 * Created by vadub on 23.08.2016.
 */
public class EffectiveSleep {
    public static void sleepNanoseconds (long nanoseconds){
        try {
            TimeUnit.NANOSECONDS.sleep(nanoseconds);
        } catch (InterruptedException e) {}
    }
}
