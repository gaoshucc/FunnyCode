package com.funnycode.blog.service;

import org.thymeleaf.context.Context;

/**
 * @author CC
 * @date 2019-09-23 07:57
 */
public interface MailService {
    /**
     * 发送HTML邮件
     * @param to 接收方
     * @param subject 邮件主题
     * @param template 邮件模板
     * @param context 邮件变量
     */
    boolean sendHtmlMail(String to, String subject, String template, Context context);
}
