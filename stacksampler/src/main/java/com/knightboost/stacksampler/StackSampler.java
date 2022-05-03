package com.knightboost.stacksampler;


import androidx.annotation.NonNull;

import com.knightboost.stacksampler.util.FastTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class StackSampler {

    private Thread targetThread;
    private int sampleInterval;
    private int maxCacheCount;
    private ScheduledExecutorService scheduler;
    private final ConcurrentLinkedQueue<StackTraceSample> stackTraceSampleQueue = new ConcurrentLinkedQueue<>();

    private final Object queueLock = new Object();

    private boolean isSampling = false;

    //default value: 20 seconds
    private final int DEFAULT_MAX_SAMPLING_TOTAL_MILL_TIME =20*1000;

    public StackSampler(final @NonNull Thread targetThread,
                        int sampleInterval) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("StackSampler");
                return thread;
            }
        });
        init(targetThread, scheduler, sampleInterval, DEFAULT_MAX_SAMPLING_TOTAL_MILL_TIME);

    }

    public StackSampler(@NonNull final Thread targetThread,
                        @NonNull final ScheduledExecutorService scheduler,
                        int sampleInterval) {
        init(targetThread, scheduler, sampleInterval, DEFAULT_MAX_SAMPLING_TOTAL_MILL_TIME);

    }

    private void init(final @NonNull Thread targetThread,
                      final @NonNull ScheduledExecutorService scheduler,
                      final int samplingInterval,
                      final int maxSamplingTotalTime) {
        checkThread(targetThread);
        checkInterval(samplingInterval);
        this.targetThread = targetThread;
        this.sampleInterval = samplingInterval;
        this.maxCacheCount = maxSamplingTotalTime / sampleInterval;
        this.scheduler = scheduler;
    }

    public synchronized void startSampling() {
        if (isSampling) {
            return;
        }
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                long wallTime = FastTimer.currentTimeMillis();
                //todo support cpu time by read /proc/pid/tid/..
                StackTraceElement[] stackTraceElements = targetThread.getStackTrace();
                StackTraceSample stackTraceSample = new StackTraceSample(wallTime, stackTraceElements);
                synchronized (queueLock){
                    if (stackTraceSampleQueue.size()>maxCacheCount){
                        stackTraceSampleQueue.poll();
                    }
                    stackTraceSampleQueue.offer(stackTraceSample);
                }

            }
        }, 0, sampleInterval, TimeUnit.MILLISECONDS);
        isSampling = true;
    }

    public synchronized void stopSampling() {
        isSampling = false;
        this.stackTraceSampleQueue.clear();
    }

    public int getSampleInterval() {
        return this.sampleInterval;
    }

    public List<StackTraceSample> getStackSamplesBetweenElapseRealTime(long beginTime, long endTime){
        long beginWallTime = FastTimer.convertElapseRealTimeToRTCTime(beginTime);
        long endWallTime = FastTimer.convertElapseRealTimeToRTCTime(endTime);
        return getStackSamplesBetweenWallTime(beginWallTime,endWallTime);
    }

    public List<StackTraceSample> getStackSamplesBetweenWallTime(long beginTime, long endTime){
        ArrayList<StackTraceSample> stackTraceSamples = null;
        synchronized (queueLock){
             stackTraceSamples = new ArrayList<>(stackTraceSampleQueue);
        }

        ArrayList<StackTraceSample> result = new ArrayList<>();

        for (int i = 0; i < stackTraceSamples.size(); i++) {
            StackTraceSample item = stackTraceSamples.get(i);
            long time = item.getTime();
            if (time>=beginTime && time<=endTime){
                result.add(item);
            }
        }
        return result;
    }

    private static void checkThread(Object o) {
        if (o == null) {
            throw new RuntimeException("targetThread can't be null");
        }
    }

    private static void checkInterval(int interval) {
        if (interval <= 0) {
            throw new RuntimeException("interval value must >0");
        }
    }

}
