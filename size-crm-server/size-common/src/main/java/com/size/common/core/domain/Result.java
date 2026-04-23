package com.size.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 失败 */
    public static final int FAIL = 500;

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> Result<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> Result<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
