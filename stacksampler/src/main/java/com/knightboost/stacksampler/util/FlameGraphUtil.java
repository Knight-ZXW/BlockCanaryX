package com.knightboost.stacksampler.util;

import com.knightboost.stacksampler.StackTraceSample;

import java.util.List;

public class FlameGraphUtil {

    /**
     * 将堆栈 转换为
     *
     * @param stackTraceElements
     * @return
     */
    static String toFlameGraphLine(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = stackTraceElements.length - 1; i >= 0; i--) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            sb.append(stackTraceElement.toString()).append(";");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public static String toFlameGraphText(List<StackTraceSample> stackTraceSamples,
                                          boolean outputContainTime) {
        if (stackTraceSamples.size() == 0)
            return "";

        StackTraceSample firstItem = stackTraceSamples.get(0);
        String groupStackText = toFlameGraphLine(firstItem.getStackTraceElements());
        long groupTime = firstItem.getTime();
        int count = 1;
        StringBuilder sb = new StringBuilder();
        if (stackTraceSamples.size() == 1) {
            if (outputContainTime) {
                sb.append(firstItem.getTime()).append(" ");
            }
            return sb.append(groupStackText)
                    .append(" ").append(count).toString();
        }
        for (int i = 1; i < stackTraceSamples.size(); i++) {
            String curStackTrace = toFlameGraphLine(stackTraceSamples.get(i)
                    .getStackTraceElements());
            if (curStackTrace.equals(groupStackText)) {
                count++;
            } else {
                //当前的堆栈和prev的堆栈不同，则 将 prev的 记录的堆栈 和次数写入 builder
                if (outputContainTime) {
                    sb.append(firstItem.getTime()).append(" ");
                }
                sb.append(groupStackText)
                        .append(" ")
                        .append(count).append("\n");
                //重置 count
                count = 1;
                groupStackText = curStackTrace;
            }
        }

        if (outputContainTime) {
            sb.append(firstItem.getTime()).append(" ");
        }
        sb.append(groupStackText)
                .append(" ")
                .append(count);
        return sb.toString();

    }

    public static String toFrameGraphText(List<StackTraceElement[]> stackTraces) {
        if (stackTraces.size() == 0)
            return "";
        String prevStackTrace = toFlameGraphLine(stackTraces.get(0));
        int count = 1;
        StringBuilder sb = new StringBuilder();
        if (stackTraces.size() == 1) {
            return sb.append(prevStackTrace).append(" ").append(count).toString();
        }
        for (int i = 1; i < stackTraces.size(); i++) {
            String curStackTrace = toFlameGraphLine(stackTraces.get(i));
            if (curStackTrace.equals(prevStackTrace)) {
                count++;
            } else {
                //当前的堆栈和prev的堆栈不同，则 将 prev的 记录的堆栈 和次数写入 builder
                sb.append(prevStackTrace).append(" ").append(count).append("\n");
                //重置 count
                count = 1;
                prevStackTrace = curStackTrace;
            }
        }
        sb.append(prevStackTrace).append(" ").append(count);
        return sb.toString();
    }

}
