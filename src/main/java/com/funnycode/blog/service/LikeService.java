package com.funnycode.blog.service;

/**
 * @author CC
 * @date 2019-10-02 11:23
 */
public interface LikeService {
    long getLikeCount(int entityType, long entityId);

    long getDislikeCount(int entityType, long entityId);

    int getLikeStatus(long userId, int entityType, long entityId);

    long like(long userId, int entityType, long entityId);

    long disLike(long userId, int entityType, long entityId);

    long like1(long userId, int entityType, long entityId);

    long disLike1(long userId, int entityType, long entityId);

    boolean getLikeStatus1(long userId, int entityType, long entityId);
}
