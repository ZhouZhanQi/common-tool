package com.mengcc.core.utils.thread;

import org.apache.poi.ss.formula.functions.T;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 定时任务线程池
 */
public class ScheduledThreadPoolUtils implements ThreadPoolUtils {

    /**
     *  线程池的对象
     */
    private ThreadPoolExecutor executor;

    private int nThread = 10;

    private ScheduledThreadPoolUtils() {
    }

    private static ScheduledThreadPoolUtils threadPool;
    /**
     * 线程池大小
     * @return
     */
    public synchronized static ScheduledThreadPoolUtils getInstance() {
        if (threadPool == null) {
            threadPool = new ScheduledThreadPoolUtils();
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
            executor = new ScheduledThreadPoolExecutor(nThread);
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
            executor = new ScheduledThreadPoolExecutor(nThread);
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
