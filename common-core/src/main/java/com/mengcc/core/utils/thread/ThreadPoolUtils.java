package com.mengcc.core.utils.thread;

import org.apache.poi.ss.formula.functions.T;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 线程池基类
 */
public interface ThreadPoolUtils {

    void execute(Runnable r);

    Future<T> submit(Callable<T> r);

    void cancel(Runnable r);
}
