package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.EntityType;
import com.funnycode.blog.model.Message;
import com.funnycode.blog.model.User;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.BlogUtil;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * @author CC
 * @date 2019-10-02 22:44
 */
@Component
public class FollowHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FollowHandler.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        //将被关注用户动态添加进用户动态列表
        System.out.println("====================将被关注用户动态添加进用户动态列表==================");
        String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId()),
               feedKey = RedisKeyUtil.getFeedKey(model.getEntityOwnerId());
        Set<Tuple> followerFeed = jedisAdapter.zrangeWithScores(feedKey, 0, Integer.MAX_VALUE);
        Map<String, Double> members = getIdsFromSet(followerFeed);
        if(members != null && !members.isEmpty()){
            System.out.println("================将被关注用户动态添加进用户动态列表1===========");
            jedisAdapter.zadds(timelineKey, members);
        }

        //向被关注用户发送系统消息
        Message message = new Message();
        message.setFromId(BlogUtil.SYSTEM);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUserByUserId(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户<a href='/user/" + user.getUserId() + "'>" + user.getNickname() + "</a>"
                    + "关注了你");
        }
        message.setType(Code.MSG_FOLLOW);
        message.setHasRead(0);
        messageService.addMessage(message);
        LOGGER.info("已发送关注信息");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }

    private Map<String, Double> getIdsFromSet(Set<Tuple> idset) {
        if(idset == null || idset.size() <= 0){
            return null;
        }
        Map<String, Double> map = new HashMap<>();
        for(Tuple tuple: idset){
            map.put(tuple.getElement(), tuple.getScore());
        }

        return map;
    }
}
