package com.funnycode.blog.service;

import java.util.List;
import java.util.Set;

/**
 * @author CC
 * @date 2019-10-02 21:30
 */
public interface FollowService {
    /**
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean follow(long userId, int entityType, long entityId);

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean unfollow(long userId, int entityType, long entityId);

    List<Long> getFollowers(int entityType, long entityId, long count);

    List<Long> getFollowers(int entityType, long entityId, long offset, long count);

    List<Long> getFollowees(long userId, int entityType, long count);

    List<Long> getFollowees(long userId, int entityType, long offset, long count);

    long getFollowerCount(int entityType, long entityId);

    long getFolloweeCount(long userId, int entityType);

    List<Long> getIdsFromSet(Set<String> idset);

    /**
     *  判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean isFollower(long userId, int entityType, long entityId);

    boolean getFollowStatus(Long userId, int entityType, long entityId);
}
