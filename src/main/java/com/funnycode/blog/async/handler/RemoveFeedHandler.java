package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.EntityType;
import com.funnycode.blog.service.FollowService;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author CC
 * @date 2019-11-10 10:06
 */
@Component
public class RemoveFeedHandler implements EventHandler {
    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel model) {
        // 为个人动态列表添加动态
        String feedKey = RedisKeyUtil.getFeedKey(model.getActorId());
        jedisAdapter.zrem(feedKey, String.valueOf(model.getEntityId()));
        // 获得所有粉丝
        List<Long> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Long.MAX_VALUE);
        followers.add(model.getActorId());
        // 给所有粉丝移除动态
        for (long follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.zrem(timelineKey, String.valueOf(model.getEntityId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.REMOVEFEED);
    }
}
