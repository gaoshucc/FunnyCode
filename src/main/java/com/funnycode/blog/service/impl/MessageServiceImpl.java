package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.MessageDAO;
import com.funnycode.blog.model.Message;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.service.SensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 站内信
 * @author CC
 * @date 2019-09-23 00:43
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    @Override
    public boolean addMessage(Message message) {
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message) > 0;
    }

    @Override
    public int getMessageCount(String conversationId) {
        return messageDAO.getMessageCount(conversationId);
    }

    @Override
    public List<Message> getConversationDetailInit(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetailInit(conversationId, offset, limit);
    }

    @Override
    public List<Message> getConversationDetail(String conversationId, int offset, int limit, Date firstTime) {
        return messageDAO.getConversationDetail(conversationId, offset, limit, firstTime);
    }

    @Override
    public List<Message> getConversationDetailRegular(String conversationId, Date lastTime) {
        return messageDAO.getConversationDetailRegular(conversationId, lastTime);
    }

    @Override
    public List<Message> getConversationList(long userId, int offset, int limit) {
        return  messageDAO.getConversationList(userId, offset, limit);
    }

    @Override
    public List<Message> listMessageByType(long userId, int type, int offset, int limit) {
        return messageDAO.listMessageByType(userId, type, offset, limit);
    }

    @Override
    public int getConversationUnreadCount(long userId, String conversationId) {
        return messageDAO.getConversationUnreadCount(userId, conversationId);
    }

    @Override
    public long getMessageUnreadCount(long userId, int type){
        long unread = messageDAO.getMessageUnreadCount(userId, type);

        return unread;
    }

    @Override
    public long getMessageAllUnreadCount(long userId){
        long unread = messageDAO.getMessageAllUnreadCount(userId);

        return unread;
    }

    @Override
    public int updateMessageHasread(long userId, int type){
        return messageDAO.updateMessageHasread(userId, type);
    }

    @Override
    public int updatePersonalMessageHasread(String conversationId, long userId) {
        return messageDAO.updatePersonalMessageHasread(conversationId, userId);
    }
}
