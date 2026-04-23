package com.size.common.core.exception;

/**
 * 业务异常
 */
public final class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private Integer code;

    /** 错误提示 */
    private String message;

    public ServiceException() {}

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
