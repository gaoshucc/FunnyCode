package com.funnycode.blog.util;

import com.funnycode.blog.model.ExceptionEnum;
import com.funnycode.blog.model.Result;

/**
 * @author CC
 * @date 2019-09-22 13:01
 */
public class ResultUtil {
    /**
     * 返回成功，传入返回体具体出參
     * @param object
     * @return
     */
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(object);
        return result;
    }

    /**
     * 提供给部分不需要出參的接口
     * @return
     */
    public static Result success(){
        return success(null);
    }

    /**
     * 自定义错误信息
     * @param code
     * @param msg
     * @return
     */
    public static Result error(Integer code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
     * 返回异常信息，在已知的范围内
     * @param exception 异常
     * @return Result
     */
    public static Result error(ExceptionEnum exception){
        Result result = new Result();
        result.setCode(exception.getCode());
        result.setMsg(exception.getMsg());
        result.setData(null);
        return result;
    }
}
