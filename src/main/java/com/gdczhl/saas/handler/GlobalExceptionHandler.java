package com.gdczhl.saas.handler;

import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.exceptions.BusinessException;
import com.gdczhl.saas.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.List;

/**
 * @author hkx
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = SQLException.class)
    public ResponseVo sqlExceptionHandler(SQLException e) {
        log.error("数据库错误：{}", e.getMessage());
        e.printStackTrace();
        return ResponseVo.fail(500, "内部错误");
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseVo BusinessExceptionHandle(BusinessException e) {
        log.error("逻辑处理错误：{}", e.getMessage());
        e.printStackTrace();
        return ResponseVo.fail(EResultCode.BusinessExceptionCode, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseVo exception(Exception e) {
        log.info("内部错误: {}", e.getMessage());
        e.printStackTrace();
        return ResponseVo.fail(500, "内部错误");
    }

    //处理接口参数校验失败
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public ResponseVo handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return ResponseVo.fail(501, sb.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseVo handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseVo.fail(EResultCode.BusinessExceptionCode, ex.getMessage());
    }

}
