package com.mengcc.core.utils.thread;

import org.apache.poi.ss.formula.functions.T;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 固定大小线程池100个
 */
public class FixedThreadPoolUtils implements ThreadPoolUtils {

    /**
     *  线程池的对象
     */
    private ThreadPoolExecutor executor;

    /**
     * 固定大小
     */
    private static final int nThreads = 50;

    private FixedThreadPoolUtils() {
    }

    private static FixedThreadPoolUtils threadPool;
    /**
     * 线程池大小
     * @return
     */
    public synchronized static FixedThreadPoolUtils getInstance() {
        if (threadPool == null) {
            threadPool = new FixedThreadPoolUtils();
        }
        return threadPool;
    }

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
            executor = new ThreadPoolExecutor(nThreads, nThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());
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
            executor = new ThreadPoolExecutor(nThreads, nThreads,
                    0L, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(20),
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


    /**
     * 获取任务数量
     * @return
     */
    public int taskSize(){
        return Objects.isNull(executor) ? 0 : executor.getActiveCount() + executor.getQueue().size() ;
    }

    /**
     * 执行完任务关闭
     */
    public void shutdown(){
        if (Objects.nonNull(executor)) {
            executor.shutdown();
        }
    }

    /**
     * 立刻关闭
     */
    public void showdownNow() {
        if (Objects.nonNull(executor)) {
            executor.shutdownNow();
        }
    }


}
