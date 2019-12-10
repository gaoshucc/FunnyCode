package com.funnycode.blog.service.impl;

import com.funnycode.blog.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author CC
 * @date 2019-09-23 07:57
 */
@Service
public class MailServiceImpl implements MailService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private JavaMailSenderImpl mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.default-encoding}")
    private String encoding;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private boolean enable;

    /**
     * 发送HTML邮件
     * @param to 接收方
     * @param subject 邮件主题
     * @param template 邮件模板
     * @param context 邮件变量
     */
    @Override
    public boolean sendHtmlMail(String to, String subject, String template, Context context) {
        MimeMessage message=mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(subject);
            //渲染模板
            String mailContent = templateEngine.process(template, context);
            helper.setText(mailContent,true);
            mailSender.send(message);
            LOGGER.info("HTML格式邮件发送成功");
            return true;
        }catch (Exception e){
            LOGGER.info("HTML格式邮件发送失败");
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setPort(port);
        mailSender.setDefaultEncoding(encoding);
        mailSender.setProtocol("smtps");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", enable);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
