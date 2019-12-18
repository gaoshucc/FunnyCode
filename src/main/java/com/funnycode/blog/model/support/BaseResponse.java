package com.funnycode.blog.model.support;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author CC
 * @date 2019-12-13 16:25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    /**
     * 响应码
     */
    private Integer status;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应开发信息
     */
    private String devMessage;

    /**
     * 响应数据
     */
    private T data;

    public BaseResponse(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * ok响应(含响应数据)
     *
     * @param data 响应数据
     * @param message 响应信息
     * @return ok
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String message, @Nullable T data) {
        return new BaseResponse<>(HttpStatus.OK.value(), message, data);
    }

    /**
     * ok响应(不含响应数据)
     *
     * @param message 响应信息
     * @return ok
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String message) {
        return ok(message, null);
    }

    /**
     * ok响应(只含响应数据)
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应
     */
    public static <T> BaseResponse<T> ok(@NonNull T data) {
        return new BaseResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }
}
