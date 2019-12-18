package com.funnycode.blog.model.vo;

import java.util.Date;

/**
 * @author CC
 * @date 2019-11-03 18:07
 */
public class FeedVO {
    private Long id;
    private Integer type;
    private UserVO user;
    private Date createdDate;
    private Long forwordCnt;
    private Long commentCnt;
    private Long likeCnt;
    private Boolean forwordState;
    private Boolean likeState;
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(Long commentCnt) {
        this.commentCnt = commentCnt;
    }

    public Long getForwordCnt() {
        return forwordCnt;
    }

    public void setForwordCnt(Long forwordCnt) {
        this.forwordCnt = forwordCnt;
    }

    public Long getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(Long likeCnt) {
        this.likeCnt = likeCnt;
    }

    public Boolean getForwordState() {
        return forwordState;
    }

    public void setForwordState(Boolean forwordState) {
        this.forwordState = forwordState;
    }

    public Boolean getLikeState() {
        return likeState;
    }

    public void setLikeState(Boolean likeState) {
        this.likeState = likeState;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
