package blockcanary;

import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import androidx.annotation.CallSuper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 参考Matrix的方式，防止Looper printer 被覆盖
 */
public class LooperMonitor implements MessageQueue.IdleHandler {

    private final List<MessageDispatchListener> listeners = new CopyOnWriteArrayList<>();

    private static final String TAG = "LooperMonitor";
    private LooperPrinter printer;
    private Looper looper;

    private static long CHECK_TIME = 2 * 1000L;
    private static final long CHECK_TIME_THRESHOLD =60*1000;

    private long lastCheckPrinterTime = 0;


    public abstract static class MessageDispatchListener {

        boolean dispatchStarted = false;

        @CallSuper
        public void onDispatchStart(String x) {
            this.dispatchStarted = true;
        }

        @CallSuper
        public void onDispatchEnd(String x) {
            this.dispatchStarted = false;
        }
    }

    private static  LooperMonitor mainMonitor = new LooperMonitor(Looper.getMainLooper());

    public static LooperMonitor ofMainThread(){
        return mainMonitor;
    }


    public List<MessageDispatchListener> getListeners() {
        return listeners;
    }

    public void addListener(MessageDispatchListener listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)){
                listeners.add(listener);
            }
        }
    }

    public void removeListener(MessageDispatchListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public LooperMonitor(Looper looper) {
        this.looper = looper;
        resetPrinter();
        addIdleHandler(looper);
    }

    @Override
    public boolean queueIdle() {
        if (SystemClock.uptimeMillis() - lastCheckPrinterTime >= CHECK_TIME) {
            resetPrinter();
            lastCheckPrinterTime = SystemClock.uptimeMillis();
        }
        return true;
    }

    public synchronized void onRelease() {
        if (printer != null) {
            synchronized (listeners) {
                listeners.clear();
            }
            Log.v(TAG,String.format("[onRelease] %s, origin printer:%s",  looper.getThread().getName(), printer.origin));
            looper.setMessageLogging(printer.origin);
            removeIdleHandler(looper);
            looper = null;
            printer = null;
        }
    }

    private static boolean isReflectLoggingError = false;

    private synchronized void resetPrinter() {
        Printer originPrinter = null;
        try {
            if (!isReflectLoggingError) {
                Field mLoggingField = looper.getClass().getDeclaredField("mLogging");
                mLoggingField.setAccessible(true);
                originPrinter = (Printer) mLoggingField.get(looper);
                if (originPrinter == printer && null != printer) {
                    return;
                }
                //如果是matrix 的Printer 则不替换，否则会互相连续替换
            }
        } catch (Exception e) {
            isReflectLoggingError = true;
            Log.e(TAG, "[resetPrinter] %s", e);
        }

        if (null != printer) {
            Log.w(TAG,String.format("maybe thread:%s printer[%s] was replace other[%s]!",
                    looper.getThread().getName(), printer, originPrinter));

        }
        CHECK_TIME = Math.max(CHECK_TIME_THRESHOLD,CHECK_TIME*2);
        looper.setMessageLogging(printer = new LooperPrinter(originPrinter));
        if (null != originPrinter) {
            Log.i(TAG,String.format("reset printer, originPrinter[%s] in %s", originPrinter, looper.getThread().getName()));
        }
    }

    private synchronized void removeIdleHandler(Looper looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            looper.getQueue().removeIdleHandler(this);
        } else {
            try {
                MessageQueue queue = getLooperQueue(looper);
                queue.removeIdleHandler(this);
            } catch (Exception e) {
                Log.e(TAG, "[removeIdleHandler] %s", e);
            }

        }
    }

    private synchronized void addIdleHandler(Looper looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            looper.getQueue().addIdleHandler(this);
        } else {
            try {
                MessageQueue queue = getLooperQueue(looper);
                queue.addIdleHandler(this);
            } catch (Exception e) {
                Log.e(TAG, "[removeIdleHandler] "+e.toString());
            }
        }
    }

    private MessageQueue getLooperQueue(Looper looper)throws Exception{
        Field mQueueField = Looper.class.getDeclaredField("mQueue");
        mQueueField.setAccessible(true);
        return ((MessageQueue) mQueueField.get(looper));
    }

    private class LooperPrinter implements Printer {
        public Printer origin;
        boolean isHasChecked = false;

        LooperPrinter(Printer printer) {
            this.origin = printer;
        }

        @Override
        public void println(String x) {
            if (null != origin) {
                origin.println(x);
                if (origin == this) {
                    throw new RuntimeException(TAG + " origin == this");
                }
            }

            if (!isHasChecked) {
                dispatch(x.charAt(0) == '>', x);
            }

        }
    }


    private void dispatch(boolean isBegin, String log) {
        for (MessageDispatchListener listener : listeners) {
            if (isBegin){
                listener.onDispatchStart(log);
            }else {
                if (listener.dispatchStarted){
                    listener.onDispatchEnd(log);
                }else {
                    //do nothing
                    //保证 start 是第一个分发给listener
                }
            }
        }

    }


}
