package com.mengcc.core.exceptions;

/**
 * @author zhouzq
 * @date 2019/8/29
 * @desc 更新数据异常
 */
public class DataUpdateException extends BaseUnCheckedException{

    public DataUpdateException(String msg) {
        super(msg);
    }

    public DataUpdateException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
