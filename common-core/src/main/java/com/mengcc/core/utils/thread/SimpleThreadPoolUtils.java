package com.mengcc.core.utils.thread;

import org.apache.poi.ss.formula.functions.T;

import java.util.concurrent.*;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 线程池工具类(根据cpu设置连接池的大小)
 */
public class SimpleThreadPoolUtils implements ThreadPoolUtils{

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE  = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     *  线程池的对象
     */
    private ThreadPoolExecutor executor;

    private SimpleThreadPoolUtils() {
    }

    private static SimpleThreadPoolUtils threadPool;

    public synchronized static SimpleThreadPoolUtils getInstance() {
        if (threadPool == null) {
            threadPool = new SimpleThreadPoolUtils();
        }
        return threadPool;
    }

    /**
     * 开启一个无返回结果的线程
     * @param r
     */
    @Override
    public void execute(Runnable r) {
        if (executor == null) {
            /**
             * corePoolSize:核心线程数
             * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
             * keepAliveTime：非核心线程闲置时间超时时长
             * unit：keepAliveTime的单位
             * workQueue：等待队列，存储还未执行的任务
             * threadFactory：线程创建的工厂
             * handler：异常处理机制
             *
             */
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }
        // 把一个任务丢到了线程池中
        executor.execute(r);
    }

    /**
     * 开启一个有返回结果的线程
     * @param r
     * @return
     */
    @Override
    public Future<T> submit(Callable<T> r) {
        if (executor == null) {
            /**
             * corePoolSize:核心线程数
             * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
             * keepAliveTime：非核心线程闲置时间超时时长
             * unit：keepAliveTime的单位
             * workQueue：等待队列，存储还未执行的任务
             * threadFactory：线程创建的工厂
             * handler：异常处理机制
             *
             */
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }
        // 把一个任务丢到了线程池中
        return executor.submit(r);
    }

    /**
     * 把任务移除等待队列
     * @param r
     */
    @Override
    public void cancel(Runnable r) {
        if (r != null) {
            executor.getQueue().remove(r);
        }
    }
}
