package com.funnycode.blog.model.vo;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-16 09:50
 */
public class FavoriteNoteVO {
    private Long id;
    private String title;
    private String type;
    private Date createTime;
    private Integer commentCnt;

    private Long userId;
    private String nickname;
    private String profile;
    private Date collectTime;

    public FavoriteNoteVO() {
    }

    public FavoriteNoteVO(Long id, String title, String type, Date createTime, Integer commentCnt, Long userId, String nickname, String profile, Date collectTime) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.createTime = createTime;
        this.commentCnt = commentCnt;
        this.userId = userId;
        this.nickname = nickname;
        this.profile = profile;
        this.collectTime = collectTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(Integer commentCnt) {
        this.commentCnt = commentCnt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }
}
