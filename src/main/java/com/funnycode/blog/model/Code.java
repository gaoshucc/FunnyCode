package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-09-18 20:46
 */
public class Code {
    /**
     * 手记状态
     * -1：彻底删除
     * 0：删除
     * 1：发布
     * 2：保存
     */
    public static final int COMPLETELY_DELETE = -1;
    public static final int DELETE = 0;
    public static final int PUBLISH = 1;
    public static final int SAVE = 2;

    /**
     * 用户账号状态
     * 0：注册未登录
     * 1：已登录过
     * 2：注销号
     */
    public static final int REGISTING = 0;
    public static final int REGISTED = 1;
    public static final int LOGOUT = 2;

    /**
     * 关注状态
     * -2：不可关注
     * -1：未关注
     * 1：已关注
     */
    public static final int UNFOLLOW = -1;
    public static final int HASFOLLOW = 1;
    public static final int BANTO_FOLLOW = -2;

    /**
     * 系统通知toId
     */
    public static final int SYSMSG_TOID = 0;
    /**
     * 0：系统通知
     * 1：私信
     * 2：关注
     * 3：点赞
     * 4: 评论或回复
     */
    public static final int MSG_SYS = 0;
    public static final int MSG_PM = 1;
    public static final int MSG_FOLLOW = 2;
    public static final int MSG_LIKE = 3;
    public static final int MSG_COMMENT = 4;

    /**
     * 1：原创
     * 2：转发
     */
    public static final int FEED_ORIGINAL = 1;
    public static final int FEED_FORWORD = 2;

    /**
     * 评论
     */
    public static final int NORMAL_COMMENT = 1;
    public static final int DELETE_COMMENT = 0;
    public static final int REPORT_COMMENT = -1;
    public static final String COMMENT_HASDELETE = "该评论已被删除";
    public static final String COMMENT_HASREPORT = "该评论因违规被举报，已被清除";

    /**
     * 性别
     */
    public static final int MALE = 1;
    public static final int FEMALE = 2;

    /**
     * 等级
     */
    public static final Long LEVEL1_NUM = 5000L;
    public static final Long LEVEL2_NUM = 10000L;
    public static final Long LEVEL3_NUM = 20000L;
    public static final Long LEVEL4_NUM = 40000L;

    public static final String LEVEL1 = "入门";
    public static final String LEVEL2 = "初级";
    public static final String LEVEL3 = "中级";
    public static final String LEVEL4 = "高级";
    public static final String LEVEL5 = "专家";
}
