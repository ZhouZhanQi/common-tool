package com.mengcc.spring.handle;

import com.mengcc.core.exceptions.ApiException;
import com.mengcc.core.exceptions.ConversionException;
import com.mengcc.core.exceptions.DataUpdateException;
import com.mengcc.core.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * API接口统一异常处理类
 * @author zhouzq
 * @date 2019/5/28
 * @desc
 */
@ResponseStatus(HttpStatus.OK)
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandle {
    
    @ExceptionHandler
    public ResponseVo handleConversionException (ConversionException ex, HttpServletRequest request) {
        log.error(">> 数据转换错误: {}", request.getRequestURI(), ex);
        String errMessage = StringUtils.isBlank(ex.getMessage()) ? "数据转换错误" : ex.getMessage();
        return ResponseVo.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), errMessage);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseVo handleApiException(ApiException ex, HttpServletRequest request) {
        log.error(">> API接口错误: {}", request.getRequestURI(), ex);
        String errMessage = StringUtils.isBlank(ex.getMessage()) ? "API接口错误" : ex.getMessage();
        return ResponseVo.fail(HttpStatus.SERVICE_UNAVAILABLE.value(), errMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseVo handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        log.error(">> 数据校验错误: {}", request.getRequestURI(), ex);
        String errMessage = StringUtils.isBlank(ex.getMessage()) ? "数据校验错误" : ex.getMessage();
        return ResponseVo.fail(HttpStatus.SERVICE_UNAVAILABLE.value(), errMessage);
    }

    @ExceptionHandler(DataUpdateException.class)
    public ResponseVo handleBusinessSpecialException(DataUpdateException ex, HttpServletRequest request){
        log.error(">> 数据更新错误：{}", request.getRequestURI(), ex);
        String errMessage = StringUtils.isBlank(ex.getMessage()) ? "系统业务特殊错误" : ex.getMessage();
        return ResponseVo.fail(ex.getErrCode(), errMessage);
    }

    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public ResponseVo handleMethodArgumentNotValidException(Exception ex, HttpServletRequest request) {
        log.error(">> 参数校验错误：{}", request.getRequestURI(), ex);

        BindingResult bindingResult = null;
        if (ex instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException)ex).getBindingResult();
        } else if (ex instanceof BindException) {
            bindingResult = ((BindException)ex).getBindingResult();
        }

        if (Objects.isNull(bindingResult)) {
            return ResponseVo.fail(HttpStatus.BAD_REQUEST.value(), "服务器内部错误，请联系管理员");
        }

        Optional<ObjectError> objectError = bindingResult.getAllErrors().stream().findFirst();

        String errMessage = !objectError.isPresent() ? "参数特殊错误" : objectError.get().getDefaultMessage();
        return ResponseVo.fail(HttpStatus.BAD_REQUEST.value(), errMessage);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseVo defaultErrorHandler(Exception ex, HttpServletRequest request) {
        log.error(">> 服务器内部异常: {}", request.getRequestURI(), ex);
        return ResponseVo.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系管理员");
    }
}
