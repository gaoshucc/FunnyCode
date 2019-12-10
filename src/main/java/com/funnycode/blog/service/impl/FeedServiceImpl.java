package com.funnycode.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.dao.FeedDAO;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.EntityType;
import com.funnycode.blog.model.Feed;
import com.funnycode.blog.service.CommentService;
import com.funnycode.blog.service.FeedService;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author CC
 * @date 2019-11-03 10:45
 */
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private FeedDAO feedDAO;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private CommentService commentService;

    @Override
    public boolean addOriginalFeed(Feed feed) {
        return feedDAO.addFeed(feed) > 0;
    }

    @Override
    public boolean addForwordFeed(Feed feed, long userId, long feedId) {
        if(feedDAO.addFeed(feed) > 0){
            jedisAdapter.sadd(RedisKeyUtil.getForwordKey(userId), String.valueOf(feedId));
            feedDAO.addFeedForwordCnt(feedId);
            return true;
        }
        return false;
    }

    @Override
    public Feed getFeedById(long id) {
        return feedDAO.getFeedById(id);
    }

    @Override
    public List<Feed> getUserFeeds(long maxId, List<Long> userIds, long count) {
        return feedDAO.getUserFeeds(maxId, userIds, count);
    }

    @Override
    public boolean addFeedCommentCnt(long feedId) {
        return feedDAO.addFeedComentCnt(feedId) > 0;
    }

    @Override
    public boolean minusFeedCommentCnt(long feedId) {
        return feedDAO.minusFeedComentCnt(feedId) > 0;
    }

    @Override
    public boolean addFeedForwordCnt(Long feedId) {
        return feedDAO.addFeedForwordCnt(feedId) > 0;
    }

    @Override
    public boolean minusFeedForwordCnt(Long feedId) {
        return feedDAO.minusFeedForwordCnt(feedId) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeFeed(Feed feed, Long userId) {
        if(feedDAO.removeFeed(feed.getId()) > 0){
            //转发
            if(feed.getType() == Code.FEED_FORWORD){
                JSONObject data = JSON.parseObject(feed.getData());
                jedisAdapter.srem(RedisKeyUtil.getForwordKey(userId), String.valueOf(data.getLong("feedId")));
                feedDAO.minusFeedForwordCnt(data.getLong("feedId"));
            }
            //删除评论
            commentService.removeAllComment(EntityType.ENTITY_FEED, feed.getId());
            //删除点赞集合
            String key = RedisKeyUtil.getLikeKey(EntityType.ENTITY_FEED, feed.getId());
            jedisAdapter.del(key);
            return true;
        }

        return false;
    }
}
