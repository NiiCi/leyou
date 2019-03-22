package com.leyou.common.utils;

import java.util.concurrent.*;

public class ThreadUtils {
    //创建一个10个线程的线程池
    private static final ExecutorService es = Executors.newFixedThreadPool(10);
    // 池中所保存的线程数，包括空闲线程。
    private final static int corePoolSize = 5;
    // 池中允许的最大线程数。
    private final static int maximumPoolSize = 10;
    // 当线程数大于核心线程时，此为终止前多余的空闲线程等待新任务的最长时间
    private final static long keepAliveTime = 200;
    //创建一个阻塞队列，用于存储等待执行的任务
    //一般使用LinkedBlockingQueue和Synchronous较多，线程池的排队策略与blockingQueue有关
    private final static SynchronousQueue<Runnable> workQueue = new SynchronousQueue<Runnable>(true);
    //private static final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2);
    private static final ThreadPoolExecutor threads = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.MINUTES,workQueue);
    public static void execute(Runnable runnable) {
        //es.submit(runnable);
        threads.execute(runnable);
    }
}
