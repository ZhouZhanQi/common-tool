package com.mengcc.core.exceptions;


import lombok.Getter;

@Getter
public class BaseUnCheckedException extends RuntimeException {

    private static final long serialVersionUID = -367881532168854586L;

    /**
     * 错误代码
     */
    protected int errCode = 500;

    public BaseUnCheckedException(int code) {
        super();
        this.errCode = code;
    }

    public BaseUnCheckedException(String msg) {
        super(msg);
    }

    public BaseUnCheckedException(String msg, int errCode) {
        super(msg);
        this.errCode = errCode;
    }

    public BaseUnCheckedException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
