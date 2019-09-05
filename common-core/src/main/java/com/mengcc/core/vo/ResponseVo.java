package com.mengcc.core.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mengcc.core.utils.JacksonUtils;
import com.mengcc.core.utils.encrypt.AesUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.MalformedParameterizedTypeException;

/**
 * 接口统一返回信息, 默认为一个成功但无任何返回数据的对象。
 *
 * @author zhouzq
 * @date 2017-12-15
 */
@ApiModel("API响应消息")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"success", "data", "securityData", "errCode", "errMsg", "errDetail"})
public class ResponseVo<T> {

    @ApiModelProperty(value = "成功标记", example = "true")
    private boolean success;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String securityKey;

    @ApiModelProperty("加密保存的字段")
    private String securityData;

    @ApiModelProperty("错误码")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errCode;

    @ApiModelProperty("错误信息")
    private String errMsg;

    @ApiModelProperty("错误详细")
    private Object errDetail;

    @ApiModelProperty("响应的数据")
    private T data;

    /**
     * 创建一个没有返回数据的成功的返回信息对象。
     *
     * @return
     */
    public static ResponseVo success() {
        return success(null);
    }

    /**
     * 创建一个成功的返回信息对象, 并设置返回数据。
     *
     * @param data 返回的数据
     * @param <T>  返回数据的类型
     * @return
     */
    public static <T> ResponseVo<T> success(T data) {
        return success(null, data);
    }

    /**
     * 创建一个成功的返回信息对象,并根据securityKey决定是否加密返回
     *
     * @param securityKey 加密密钥,当密钥不为空时,将进行加密,返回中的data将替换为securityData
     * @param data        返回数据
     * @param <T>         返回数据类型
     * @return
     */
    public static <T> ResponseVo<T> success(String securityKey, T data) {
        ResponseVo<T> vo = new ResponseVo<>();
        vo.success = true;
        vo.setSecurityKey(securityKey);
        vo.setData(data);
        return vo;
    }

    /**
     * 创建一个失败的返回信息对象, 并设置错误信息。
     *
     * @param msg 错误信息
     * @return
     */
    public static <T> ResponseVo<T> fail(String msg) {
        return fail(0, msg);
    }

    /**
     * 创建一个失败的返回信息对象, 并设置错误代码和错误信息。
     *
     * @param errCode 错误代码
     * @param errMsg  错误信息
     * @return
     */
    public static <T> ResponseVo<T> fail(int errCode, String errMsg) {
        return fail(errCode, errMsg, null);
    }

    /**
     * 创建一个失败的返回信息对象, 并设置错误代码，错误信息和错误详细。
     *
     * @param errCode   错误代码
     * @param errMsg    错误消息
     * @param errDetail 错误详细
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ResponseVo<T> fail(int errCode, String errMsg, Object errDetail) {
        ResponseVo vo = new ResponseVo();
        vo.success = false;
        vo.errCode = errCode;
        vo.errMsg = errMsg;
        vo.errDetail = errDetail;
        return vo;
    }

    public ResponseVo() {
    }

    public ResponseVo(String securityKey) {
        this.securityKey = securityKey;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getSecurityData() {
        return securityData;
    }

    public void setSecurityData(String securityData) {
        this.securityData = securityData;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Object getErrDetail() {
        return errDetail;
    }

    public void setErrDetail(Object errDetail) {
        this.errDetail = errDetail;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        if (StringUtils.isBlank(securityKey)) {
            //不加密
            this.data = data;
        } else {
            //加密
            if (data instanceof String) {
                securityData = AesUtil.aesEncode(String.valueOf(data), securityKey);
            } else {
                securityData = AesUtil.aesEncode(JacksonUtils.pojo2json(data), securityKey);
            }
        }
    }
}
