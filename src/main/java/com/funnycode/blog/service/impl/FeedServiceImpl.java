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
        return addFeed(feed);
    }

    @Override
    public boolean addForwordFeed(Feed feed, long userId, long feedId) {
        if(addFeed(feed)){
            jedisAdapter.sadd(RedisKeyUtil.getForwordKey(userId), String.valueOf(feedId));
            return updateForwordCntById(feedId, 1L);
        }
        return false;
    }

    private boolean addFeed(Feed feed){
        return feedDAO.add(feed) > 0;
    }

    @Override
    public Feed getFeedById(long id) {
        return feedDAO.getById(id);
    }

    @Override
    public List<Feed> getUserFeeds(long maxId, List<Long> userIds, long count) {
        return feedDAO.findAllByIds(maxId, userIds, count);
    }

    @Override
    public boolean addFeedCommentCnt(long feedId) {
        return feedDAO.updateCommentCntById(feedId, 1L) > 0;
    }

    @Override
    public boolean minusFeedCommentCnt(long feedId) {
        return feedDAO.updateCommentCntById(feedId, -1L) > 0;
    }

    @Override
    public boolean addFeedForwordCnt(long feedId) {
        return updateForwordCntById(feedId, 1L);
    }

    @Override
    public boolean minusFeedForwordCnt(long feedId) {
        return updateForwordCntById(feedId, -1L);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeFeed(Feed feed, long userId) {
        if(feedDAO.removeById(feed.getId()) > 0){
            //转发
            if(feed.getType() == Code.FEED_FORWORD){
                JSONObject data = JSON.parseObject(feed.getData());
                jedisAdapter.srem(RedisKeyUtil.getForwordKey(userId), String.valueOf(data.getLong("feedId")));
                updateForwordCntById(data.getLong("feedId"), -1L);
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

    @Override
    public boolean updateForwordCntById(long feedId, long offset){
        return feedDAO.updateForwordCntById(feedId, offset) > 0;
    }
}
