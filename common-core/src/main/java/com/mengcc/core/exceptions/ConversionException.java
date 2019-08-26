package com.mengcc.core.exceptions;

/**
 * 转换器异常
 *
 * @author ZhouZQ
 * @create 2019/3/22
 */
public class ConversionException extends BaseUnCheckedException {

	private static final long serialVersionUID = -2674586039653733856L;

	/**
	 * 构造方法，构造带异常信息的ConversionException
	 * 
	 * @param msg
	 */
	public ConversionException(String msg) {
		super(msg);
	}

	public ConversionException(String msg, Throwable t) {
		super(msg, t);
	}
}
