package com.gattaca.bitalinoecgchartwithlibrary;

/**
 * Created by vadub on 26.07.2016.
 */
public class SimpleECG {
    private double millivolt;
    private int analogSignal;
    private int bitRate;
    static final double VCC = 3.3;
    static final double GECG = 1100;

    public double get() {
        return millivolt;
    }

    public int getAnalog() {
        return analogSignal;
    }

    SimpleECG(int analog, int bit) {
        bitRate = bit;
        analogSignal = analog;
        millivolt = convert(analogSignal, bitRate);
    }

    SimpleECG(int analog) {
        this(analog, 10);
    }

    SimpleECG() {
        this(0);
    }

    public static double convert(int analog) {
        return convert(analog, 10);
    }

    public static double convert(int analog, int bitRate) {
        return 1000 * ((double)analog / (1 << bitRate) - 0.5) * VCC / GECG;
    }

}
