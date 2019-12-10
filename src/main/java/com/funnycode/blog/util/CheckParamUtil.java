package com.funnycode.blog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CC
 * @date 2019-09-23 22:25
 */
public class CheckParamUtil {
    /**
     * 校验邮箱地址
     */
    public static boolean checkEmail(String email){
        String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        return m.matches();
    }

    /**
     * 检测是否包含特殊字符
     * @param str 所要检测的字符串
     * @return true：存在特殊字符，false：不存在特殊字符
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _.`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
