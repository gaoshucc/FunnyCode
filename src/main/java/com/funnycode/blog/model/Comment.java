package com.funnycode.blog.model;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-02 00:40
 */
public class Comment {
    private long id;
    private long userId;
    private int entityType;
    private long entityId;
    private String content;
    private Date createTime;

    /**
     * 1: 正常
     * 0：删除
     * -1：违规
     */
    private int status;
    private long parentId;

    public Comment() {
    }

    public Comment(long userId, int entityType, long entityId, String content, Date createTime, int status, long parentId) {
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.content = content;
        this.createTime = createTime;
        this.status = status;
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
