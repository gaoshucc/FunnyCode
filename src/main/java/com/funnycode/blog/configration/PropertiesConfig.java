package com.funnycode.blog.configration;

import com.funnycode.blog.util.PropertiesUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author CC
 * @date 2019-10-15 20:09
 */
@Configuration
public class PropertiesConfig {
    @Resource
    private Environment env;

    @PostConstruct
    public void setProperties() {
        PropertiesUtil.setEnvironment(env);
    }
}
