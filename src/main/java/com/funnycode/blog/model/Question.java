package com.funnycode.blog.model;

import java.util.Date;

/**
 * @author CC
 * @date 2019-12-12 18:53
 */
public class Question {
    private long id;
    private String title;
    private int type;
    private Long userId;
    private Date createTime;
    private int status;
    private String content;
    private int answerCnt;

    public Question(String title, int type, Long userId, Date createTime, int status, String content, int answerCnt) {
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.createTime = createTime;
        this.status = status;
        this.content = content;
        this.answerCnt = answerCnt;
    }

    public Question(long id, String title, int type, Long userId, Date createTime, String content) {
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

    public int getAnswerCnt() {
        return answerCnt;
    }

    public void setAnswerCnt(int answerCnt) {
        this.answerCnt = answerCnt;
    }
}
