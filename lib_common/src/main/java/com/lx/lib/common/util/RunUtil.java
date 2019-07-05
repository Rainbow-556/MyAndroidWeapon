package com.lx.lib.common.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by lixiang on 2019/1/16.<br/>
 */
public final class RunUtil {
    public interface WorkCallback<T> {
        void onDone(T data);
    }

    public interface Work<T> {
        T execute();
    }

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    public static Handler getUiHandler() {
        return UI_HANDLER;
    }

    public static boolean isInUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void runOnUiThread(Runnable run) {
        runOnUiThread(run, 0);
    }

    public static void runOnUiThread(Runnable run, long delay) {
        if (run == null) {
            return;
        }
        if (isInUiThread() && delay <= 0) {
            run.run();
        } else {
            UI_HANDLER.postDelayed(run, delay);
        }
    }

    public static void runOnWorkerThread(Work work) {
        runOnWorkerThread(work, null);
    }

    public static <T> void runOnWorkerThread(final Work<T> work, final WorkCallback<T> callback) {
        runOnWorkerThread(work, callback, 0);
    }

    public static <T> void runOnWorkerThread(final Work<T> work, final WorkCallback<T> callback, long delay) {
        if (work == null) {
            return;
        }
        UI_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        final T data = work.execute();
                        if (callback != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(data);
                                }
                            });
                        }
                    }
                });
            }
        }, delay);
    }
}
