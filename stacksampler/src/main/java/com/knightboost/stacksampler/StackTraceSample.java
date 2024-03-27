package com.knightboost.stacksampler;

import android.util.Log;

import java.io.Serializable;

public class StackTraceSample implements Serializable {
    private final long wallTime;
    private final long cpuTime;
    public StackTraceElement[] stackTraceElements;

    public StackTraceSample(long time,
                            long cpuTime,
                            StackTraceElement[] stackTraceElements) {
        this.wallTime = time;
        this.cpuTime = cpuTime;
        if (cpuTime>0){
            Log.d("zxw","cpu时间间隔为"+cpuTime+"  时钟间隔为"+wallTime);
        }
        this.stackTraceElements = stackTraceElements;
    }

    public long getWallTime() {
        return wallTime;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }
}
