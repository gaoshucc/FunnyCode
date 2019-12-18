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
        return messageDAO.add(message) > 0;
    }

    @Override
    public int getMessageCount(String conversationId) {
        return messageDAO.countByConversationId(conversationId);
    }

    @Override
    public List<Message> getConversationDetailInit(String conversationId, int offset, int limit) {
        return messageDAO.findLimitByConversationId(conversationId, offset, limit);
    }

    @Override
    public List<Message> getConversationDetail(String conversationId, int offset, int limit, Date firstTime) {
        return messageDAO.findPreviouslyByConversationId(conversationId, offset, limit, firstTime);
    }

    @Override
    public List<Message> getConversationDetailRegular(String conversationId, Date lastTime) {
        return messageDAO.findNewestByConversationId(conversationId, lastTime);
    }

    @Override
    public List<Message> getConversationList(long userId, int offset, int limit) {
        return  messageDAO.findAllConversationByUserId(userId, offset, limit);
    }

    @Override
    public List<Message> listMessageByType(long userId, int type, int offset, int limit) {
        return messageDAO.findAllByUserIdAndType(userId, type, offset, limit);
    }

    @Override
    public int getConversationUnreadCount(long userId, String conversationId) {
        return messageDAO.countUnreadByUserIdAndConversationId(userId, conversationId);
    }

    @Override
    public long getMessageUnreadCount(long userId, int type){
        long unread = messageDAO.countUnreadByUserIdAndType(userId, type);

        return unread;
    }

    @Override
    public long getMessageAllUnreadCount(long userId){
        return messageDAO.countUnreadByUserId(userId);
    }

    @Override
    public int updateMessageHasread(long userId, int type){
        return messageDAO.updateHasreadByUserIdAndType(userId, type);
    }

    @Override
    public int updatePersonalMessageHasread(String conversationId, long userId) {
        return messageDAO.updateHasreadByUserIdAndConversationId(conversationId, userId);
    }
}
