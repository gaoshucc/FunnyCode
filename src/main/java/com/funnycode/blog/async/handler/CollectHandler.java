package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.Message;
import com.funnycode.blog.model.Note;
import com.funnycode.blog.model.User;
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
 * @date 2019-10-02 18:51
 */
@Component
public class CollectHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        //向被点赞用户发送系统通知
        User actor = userService.getUserByUserId(model.getActorId());
        Note note = noteService.getNote(model.getEntityId());
        Message message = new Message();
        message.setFromId(BlogUtil.SYSTEM);
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + actor.getNickname() + "收藏了你的手记"
                + "<a href='/note/" + note.getId() + "'>" + note.getTitle() + "</a>"
                );
        message.setHasRead(0);
        message.setCreatedDate(new Date());
        message.setType(Code.MSG_SYS);
        messageService.addMessage(message);
        LOGGER.info("已发送收藏信息");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COLLECT);
    }
}
