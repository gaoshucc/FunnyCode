package com.funnycode.blog.async.handler;

import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author CC
 * @date 2019-11-10 10:34
 */
@Component
public class UnfollowHandler implements EventHandler {
    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel model) {
        //将被取关用户动态从用户动态列表移除
        String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId()),
               feedKey = RedisKeyUtil.getFeedKey(model.getEntityOwnerId());
        Set<String> followerFeed = jedisAdapter.zrange(feedKey, 0, Integer.MAX_VALUE);
        String[] members = getIdsFromSet(followerFeed);
        if(members != null && members.length > 0){
            jedisAdapter.zrem(timelineKey, members);
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.UNFOLLOW);
    }

    private String[] getIdsFromSet(Set<String> idset) {
        if(idset == null || idset.size() <= 0){
            return null;
        }
        List<String> ids = new ArrayList<>(idset);
        return ids.toArray(new String[0]);
    }
}
