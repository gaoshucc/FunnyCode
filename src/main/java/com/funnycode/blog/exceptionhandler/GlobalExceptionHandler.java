package com.funnycode.blog.exceptionhandler;

import com.funnycode.blog.model.CustomException;
import com.funnycode.blog.model.ExceptionEnum;
import com.funnycode.blog.model.Result;
import com.funnycode.blog.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author CC
 * @date 2019-09-22 10:12
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exceptionGet(Exception e){
        if(e instanceof CustomException){
            CustomException myException = (CustomException) e;
            return ResultUtil.error(myException.getCode(),myException.getMessage());
        }
        e.printStackTrace();
        LOGGER.error("[系统异常]={}", e.getMessage());
        return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
    }





/*
    *//**
     * 方法参数校验
     *//*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(e.getMessage(), e);
        return new Result(Code.PARAM_FAIL, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    *//**
     * ValidationException
     *//*
    @ExceptionHandler(ValidationException.class)
    public Result handleValidationException(ValidationException e) {
        logger.error(e.getMessage(), e);
        return new Result(Code.PARAM_FAIL, e.getCause().getMessage());
    }

    *//**
     * ConstraintViolationException
     *//*
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException e) {
        logger.error(e.getMessage(), e);
        return new Result(Code.PARAM_FAIL, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Result(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return new Result(Code.PARAM_FAIL, "数据重复，请检查后提交");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Result(Code.SER_EOR, "系统繁忙,请稍后再试");
    }*/
}
