package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-09-22 13:18
 */
public enum ExceptionEnum {

    //未知异常
    UNKNOWN_EOR(-1, "未知异常"),
    //参数异常
    PARAM_FAIL(1001, "参数异常"),
    //服务器繁忙
    SER_EOR(1002, "服务器繁忙"),
    //权限不足
    PERMISSION_DENY(1003, "权限不足");

    private Integer code;

    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
