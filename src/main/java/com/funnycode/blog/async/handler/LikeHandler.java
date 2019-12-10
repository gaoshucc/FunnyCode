package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.service.FeedService;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.BlogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-10-02 14:48
 */
@Component
public class LikeHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LikeHandler.class);

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
        //向被点赞用户发送系统通知
        User actor = userService.getUserByUserId(model.getActorId());
        Message message = new Message();
        message.setFromId(BlogUtil.SYSTEM);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        if(model.getEntityType() == EntityType.ENTITY_NOTE){
            Note note = noteService.getNote(model.getEntityId());
            message.setContent("用户<a href='/user/"+ actor.getUserId() +"'>" + actor.getNickname() + "</a>赞了你的手记"
                    + "<a href='/note/" + note.getId() + "'>" + note.getTitle() + "</a>"
            );
        }else if(model.getEntityType() == EntityType.ENTITY_FEED){
            //Feed feed = feedService.getFeedById(model.getEntityId());
            message.setContent("用户<a href='/user/"+ actor.getUserId() +"'>" + actor.getNickname() + "</a>赞了你的动态");
        }
        message.setHasRead(0);
        message.setType(Code.MSG_LIKE);
        messageService.addMessage(message);
        LOGGER.info("已发送赞信息");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
