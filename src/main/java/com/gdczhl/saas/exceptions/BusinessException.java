package com.gdczhl.saas.exceptions;

import com.gdczhl.saas.enums.EResultCode;

/**
 * @author hkx
 */
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String msg) {
        this(msg, 500);
    }


    public BusinessException(EResultCode code) {
        this(code.getMessage(), code.getCode());
    }

    public int getCode() {
        return code;
    }
}
