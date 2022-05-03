package blockcanary;

public class BlockCanaryConfig {
    /**
     * 消息处理阻塞的判定阈值
     */
    private int blockThresholdTime;
    /**
     * 消息阻塞的最大阈值，超过该阈值时 会立即生成 blocking 事件
     */
    private int blockMaxThresholdTime;

    /**
     * 堆栈采样间隔
     */
    private int stackSampleInterval;

    /**
     * 堆栈采样的最大缓存数量
     */
    private int maxStackSampleCacheCount;

    /**
     * 最多保存的卡顿数据文件
     */
    private int maxCacheBlockingFiles;

    /**
     * 在Debugger的时候是否进行卡顿检测
     */
    private boolean detectWhenDebuggerConnected;

    private BlockCanaryConfig(Builder builder) {
        setBlockThresholdTime(builder.blockThresholdTime);
        setBlockMaxThresholdTime(builder.blockMaxThresholdTime);
        setStackSampleInterval(builder.stackSampleInterval);
        setMaxStackSampleCacheCount(builder.maxStackSampleCacheCount);
        setMaxCacheBlockingFiles(builder.maxCacheBlockingFiles);
        setDetectWhenDebuggerConnected(builder.detectWhenDebuggerConnected);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(BlockCanaryConfig copy) {
        Builder builder = new Builder();
        builder.blockThresholdTime = copy.getBlockThresholdTime();
        builder.blockMaxThresholdTime = copy.getBlockMaxThresholdTime();
        builder.stackSampleInterval = copy.getStackSampleInterval();
        builder.maxStackSampleCacheCount = copy.getMaxStackSampleCacheCount();
        builder.maxCacheBlockingFiles = copy.getMaxCacheBlockingFiles();
        builder.detectWhenDebuggerConnected = copy.isDetectWhenDebuggerConnected();
        return builder;
    }

    public int getBlockThresholdTime() {
        return blockThresholdTime;
    }

    public void setBlockThresholdTime(int blockThresholdTime) {
        this.blockThresholdTime = blockThresholdTime;
    }

    public int getBlockMaxThresholdTime() {
        return blockMaxThresholdTime;
    }

    public void setBlockMaxThresholdTime(int blockMaxThresholdTime) {
        this.blockMaxThresholdTime = blockMaxThresholdTime;
    }

    public int getStackSampleInterval() {
        return stackSampleInterval;
    }

    public void setStackSampleInterval(int stackSampleInterval) {
        this.stackSampleInterval = stackSampleInterval;
    }

    public int getMaxStackSampleCacheCount() {
        return maxStackSampleCacheCount;
    }

    public void setMaxStackSampleCacheCount(int maxStackSampleCacheCount) {
        this.maxStackSampleCacheCount = maxStackSampleCacheCount;
    }

    public int getMaxCacheBlockingFiles() {
        return maxCacheBlockingFiles;
    }

    public void setMaxCacheBlockingFiles(int maxCacheBlockingFiles) {
        this.maxCacheBlockingFiles = maxCacheBlockingFiles;
    }


    public boolean isDetectWhenDebuggerConnected() {
        return detectWhenDebuggerConnected;
    }

    public void setDetectWhenDebuggerConnected(boolean detectWhenDebuggerConnected) {
        this.detectWhenDebuggerConnected = detectWhenDebuggerConnected;
    }

    public static final class Builder {
        private int blockThresholdTime =16*15;
        private int blockMaxThresholdTime = 5000;
        private int stackSampleInterval =50;
        private int maxStackSampleCacheCount=110;
        private int maxCacheBlockingFiles = 50;
        private boolean detectWhenDebuggerConnected = false;

        private Builder() {
        }

        public Builder blockThresholdTime(int val) {
            blockThresholdTime = val;
            return this;
        }

        public Builder blockMaxThresholdTime(int val) {
            blockMaxThresholdTime = val;
            return this;
        }

        public Builder stackSampleInterval(int val) {
            stackSampleInterval = val;
            return this;
        }

        public Builder maxStackSampleCacheCount(int val) {
            maxStackSampleCacheCount = val;
            return this;
        }


        public Builder maxCacheBlockingFiles(int val) {
            maxCacheBlockingFiles = val;
            return this;
        }

        public Builder detectWhenDebuggerConnected(boolean val) {
            detectWhenDebuggerConnected = val;
            return this;
        }

        public BlockCanaryConfig build() {
            return new BlockCanaryConfig(this);
        }
    }
}
