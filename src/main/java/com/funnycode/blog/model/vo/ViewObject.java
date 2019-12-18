package com.funnycode.blog.model.vo;

import java.util.HashMap;
import java.util.Map;
/**
 * @author CC
 * @date 2019-10-14 09:46
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
