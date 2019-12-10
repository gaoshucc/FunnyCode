package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.Message;
import com.funnycode.blog.service.MailService;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.util.BlogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-09-23 14:29
 */
@Component
public class RegistHandler implements EventHandler {

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        //向新注册用户发送系统通知
        Message message = new Message();
        message.setFromId(BlogUtil.SYSTEM);
        message.setToId(model.getActorId());
        message.setContent("新用户你好，您已注册成功");
        message.setHasRead(0);
        message.setCreatedDate(new Date());
        message.setType(Code.MSG_SYS);
        messageService.addMessage(message);

        //todo 给新注册用户发送邮件
        /*
        if(model.getExts().containsKey("email") && CheckParamUtil.checkEmail(model.getExt("email"))){
            Context context = new Context();
            context.setVariable("username", model.getExt("username"));
            mailService.sendHtmlMail(model.getExt("email"), "注册成功", "regist", context);
        }
        */
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.REGIST);
    }
}
