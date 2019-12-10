package com.funnycode.blog.model;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-04 21:40
 */
public class Report {
    private long id;
    private long actorId;
    private int entityType;
    private long entityId;
    private String reasons;
    private String description;
    private Date reportTime;
    /**
     * 1：举报成功
     * 0：举报中
     * -1：举报失败
     */
    private int status;

    public Report() {
    }

    public Report(long actorId, int entityType, long entityId, String reasons, String description, Date reportTime, int status) {
        this.actorId = actorId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.reasons = reasons;
        this.description = description;
        this.reportTime = reportTime;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getActorId() {
        return actorId;
    }

    public void setActorId(long actorId) {
        this.actorId = actorId;
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

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
