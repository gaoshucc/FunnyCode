package com.funnycode.blog.util;

import org.springframework.core.env.Environment;

/**
 * @author CC
 * @date 2019-10-15 20:08
 */
public class PropertiesUtil {
    private static Environment env = null;

    public static void setEnvironment(Environment env) {
        PropertiesUtil.env = env;
    }

    public static String getProperty(String key) {
        return PropertiesUtil.env.getProperty(key);
    }
}
