package com.gdczhl.saas.exceptions;

import com.gdczhl.saas.enums.EResultCode;

public class LoginException extends RuntimeException {
    private int code;

    public LoginException() {
        super(EResultCode.UNAUTHORIZED.getMessage());
        code = EResultCode.UNAUTHORIZED.getCode();
    }

    public int getCode() {
        return code;
    }
}
