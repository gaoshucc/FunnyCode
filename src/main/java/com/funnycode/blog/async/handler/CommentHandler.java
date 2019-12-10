package com.funnycode.blog.async.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.service.FeedService;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.BlogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-10-03 21:41
 */
@Component
public class CommentHandler implements EventHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(BlogUtil.SYSTEM);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUserByUserId(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_NOTE) {
            Note note = noteService.getNote(model.getEntityId());
            message.setContent("用户<a href='/user/" + user.getUserId() + "'>" + user.getNickname() + "</a>"
                    + "评论了你的手记<a href='/note/" + model.getEntityId() + "'>" + note.getTitle() + "</a>");
        }else if(model.getEntityType() == EntityType.ENTITY_FEED){
            Feed feed = feedService.getFeedById(model.getEntityId());
            JSONObject data = JSON.parseObject(feed.getData());
            message.setContent("用户<a href='/user/" + user.getUserId() + "'>" + user.getNickname() + "</a>"
                    + "评论了你的动态<a href='/feed/" + model.getEntityId() + "'>" + data.getString("content") + "</a>");
        }
        message.setHasRead(0);
        message.setType(Code.MSG_COMMENT);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
