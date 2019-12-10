package com.funnycode.blog.configration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author CC
 * @date 2019-10-19 16:20
 */
@Configuration
public class Constants {

    /**
     * token
     */
    @Value("${token}")
    private String TOKEN;

    /**
     * QQ登录相关常量
     */
    @Value("${qq.AppID}")
    private String QQ_APPID;

    @Value("${qq.Appkey}")
    private String QQ_APPKEY;

    @Value("${qq.RedirectURL}")
    private String QQ_REDIRECTURL;

    @Value("${qq.state}")
    private String QQ_STATE;

    public String getQQ_APPID() {
        return QQ_APPID;
    }

    public void setQQ_APPID(String QQ_APPID) {
        this.QQ_APPID = QQ_APPID;
    }

    public String getQQ_APPKEY() {
        return QQ_APPKEY;
    }

    public void setQQ_APPKEY(String QQ_APPKEY) {
        this.QQ_APPKEY = QQ_APPKEY;
    }

    public String getQQ_REDIRECTURL() {
        return QQ_REDIRECTURL;
    }

    public void setQQ_REDIRECTURL(String QQ_REDIRECTURL) {
        this.QQ_REDIRECTURL = QQ_REDIRECTURL;
    }

    public String getQQ_STATE() {
        return QQ_STATE;
    }

    public void setQQ_STATE(String QQ_STATE) {
        this.QQ_STATE = QQ_STATE;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }
}
