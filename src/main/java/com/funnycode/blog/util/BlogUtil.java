package com.funnycode.blog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-22 10:59
 */
public class BlogUtil {
    private static final Logger logger = LoggerFactory.getLogger(BlogUtil.class);

    public static final long SYSTEM = 1;
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String VO = "vo";

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String toJSONString(Result result) {
        JSONObject json = new JSONObject();
        json.put(CODE, result.getCode());
        json.put(MSG, result.getMsg());
        json.put(VO, JSON.toJSONString(result.getData()));
        return json.toJSONString();
    }

    public static Map<String, Object> buildRetMsg(int code, String msg){
        Map<String, Object> map = new HashMap<>(2);
        map.put(CODE, code);
        map.put(MSG, msg);
        return map;
    }

    public static String MD5(String key) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }

    public static String getLevel(Long experience){
        String level;
        if(experience < Code.LEVEL1_NUM){
            level = Code.LEVEL1;
        }else if(experience < Code.LEVEL2_NUM){
            level = Code.LEVEL2;
        }else if(experience < Code.LEVEL3_NUM){
            level = Code.LEVEL3;
        }else if(experience < Code.LEVEL4_NUM){
            level = Code.LEVEL4;
        }else {
            level = Code.LEVEL5;
        }

        return level;
    }
}
