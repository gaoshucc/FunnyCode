package com.funnycode.blog.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author CC
 * @date 2019-12-13 21:50
 */
public class ExceptionUtils {
    /**
     * 从Throwable获取堆栈信息
     * @param throwable 捕获到的异常
     * @return 堆栈信息
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
