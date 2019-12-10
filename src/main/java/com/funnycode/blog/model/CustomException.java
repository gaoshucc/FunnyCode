package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-09-22 13:30
 */
public class CustomException extends RuntimeException {
    private Integer code;

    /**
     * 继承exception，加入错误状态值
     * @param exception 已知异常
     */
    public CustomException(ExceptionEnum exception) {
        super(exception.getMsg());
        this.code = exception.getCode();
    }

    /**
     * 自定义错误信息
     * @param message
     * @param code
     */
    public CustomException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
