package com.funnycode.blog.model;

import java.util.Date;

/**
 * @author CC
 * @date 2019-09-20 22:24
 */
public class Note {
    private Long id;
    private String title;
    private Integer type;
    private Long userId;
    private Date createTime;
    private Integer status;
    private String content;
    private Integer commentCnt;

    public Note() {
    }

    public Note(String title, int type, long userId, Date createTime, int status, String content, int commentCnt) {
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.createTime = createTime;
        this.status = status;
        this.content = content;
        this.commentCnt = commentCnt;
    }

    public Note(long id, String title, int type, long userId, Date createTime, String content) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.createTime = createTime;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }
}
