package com.funnycode.blog.async;

/**
 * @author CC
 * @date 2019-09-22 23:58
 */
public enum EventType {
    //点赞
    LIKE(0),
    //评论
    COMMENT(1),
    //登录
    LOGIN(2),
    //发邮件
    MAIL(3),
    //关注
    FOLLOW(4),
    //取消关注
    UNFOLLOW(5),
    //日志
    LOG(6),
    //注册
    REGIST(7),
    //收藏
    COLLECT(8),
    //动态
    FEED(9),
    //删除动态之后移除好友动态列表的动态
    REMOVEFEED(10);

    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }
}
