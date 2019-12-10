package com.funnycode.blog.util;

/**
 * @author CC
 * @date 2019-09-23 00:04
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";

    /**
     * 点赞
     */
    private static String BIZ_COLLECT = "COLLECT";
    /**
     * 点赞
     */
    private static String BIZ_LIKE = "LIKE";
    /**
     * 踩
     */
    private static String BIZ_DISLIKE = "DISLIKE";
    /**
     * 获取粉丝
     */
    private static String BIZ_FOLLOWER = "FOLLOWER";
    /**
     * 获取关注对象
     */
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    /**
     * 时间轴
     */
    private static String BIZ_TIMELINE = "TIMELINE";
    /**
     * 个人动态
     */
    private static String BIZ_FEED = "FEED";
    /**
     * 浏览量
     */
    private static String BIZ_VISIT = "VISIT";

    /**
     * 用户转发的动态
     */
    private static String BIZ_FORWORD = "FORWORD";

    public static String getCollectKey(long userId, int entityType) {
        return BIZ_COLLECT + SPLIT + userId + SPLIT + entityType;
    }

    public static String getLikeKey(int entityType, long entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, long entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getFollowerKey(int entityType, long entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getFolloweeKey(long userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getTimelineKey(long userId) {
        return BIZ_TIMELINE + SPLIT + userId;
    }

    public static String getFeedKey(long userId){
        return BIZ_FEED + SPLIT + userId;
    }

    public static String getVisitKey(int entityType){
        return BIZ_VISIT + SPLIT + entityType;
    }

    public static String getForwordKey(Long entityId){
        return BIZ_FORWORD + SPLIT + entityId;
    }
}
