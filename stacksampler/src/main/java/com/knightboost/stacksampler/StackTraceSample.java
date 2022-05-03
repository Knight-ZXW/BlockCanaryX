package com.knightboost.stacksampler;

import java.io.Serializable;

public class StackTraceSample implements Serializable {
    private long time;
    public StackTraceElement[] stackTraceElements;

    public StackTraceSample(long time, StackTraceElement[] stackTraceElements) {
        this.time = time;
        this.stackTraceElements = stackTraceElements;
    }

    public long getTime() {
        return time;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }
}
