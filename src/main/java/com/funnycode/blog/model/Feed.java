package com.funnycode.blog.model;

import java.util.Date;

/**
 * @author CC
 * @date 2019-11-03 10:26
 */
public class Feed {
    private Long id;
    private Integer type;
    private Long userId;
    private Date createdDate;
    private Long forwordCnt;
    private Long commentCnt;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
