package com.lx.lib.java;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by glennli on 2019/9/4.<br/>
 */
public final class ThreadPoolUtil {
    private static volatile Executor EXECUTOR;
    private static final AtomicInteger sThreadCount = new AtomicInteger();

    private static Executor getExecutor() {
        if (EXECUTOR == null) {
            synchronized (ThreadPoolUtil.class) {
                if (EXECUTOR == null) {
                    ThreadFactory factory = new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "ThreadPoolUtil-" + sThreadCount.addAndGet(1));
                        }
                    };
                    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
                            5, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(),
                            factory);
                    executor.allowCoreThreadTimeOut(true);
                    EXECUTOR = executor;
                }
            }
        }
        return EXECUTOR;
    }

    public static void execute(Runnable runnable) {
        if (runnable != null) {
            getExecutor().execute(runnable);
        }
    }
}
