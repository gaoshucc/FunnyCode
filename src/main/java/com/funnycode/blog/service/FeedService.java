package com.funnycode.blog.service;

import com.funnycode.blog.model.Feed;

import java.util.List;

/**
 * @author CC
 * @date 2019-11-03 10:43
 */
public interface FeedService {
    boolean addOriginalFeed(Feed feed);

    boolean addForwordFeed(Feed feed, long userId, long feedId);

    Feed getFeedById(long id);

    List<Feed> getUserFeeds(long maxId, List<Long> userIds, long count);

    boolean addFeedCommentCnt(long feedId);

    boolean minusFeedCommentCnt(long feedId);

    boolean addFeedForwordCnt(Long feedId);

    boolean minusFeedForwordCnt(Long feedId);

    boolean removeFeed(Feed feed, Long userId);
}
