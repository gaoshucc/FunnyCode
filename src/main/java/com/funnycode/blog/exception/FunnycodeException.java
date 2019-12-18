package com.funnycode.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author CC
 * @date 2019-12-12 20:18
 */
public abstract class FunnycodeException extends RuntimeException {

    private Object errorData;

    public FunnycodeException(String message){
         super(message);
    }

    public FunnycodeException(String message, Throwable cause){
        super(message, cause);
    }

    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    /**
     * 设置错误数据
     *
     * @param errorData 错误数据
     * @return 当前异常
     */
    @NonNull
    public FunnycodeException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}
