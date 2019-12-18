package com.funnycode.blog.model.vo;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-06 20:50
 */
public class NoteVO {
    private long id;
    private String title;
    private String type;
    private Date createTime;
    private String content;
    private int commentCnt;

    public NoteVO() {
    }

    public NoteVO(long id, String title, String type, Date createTime, String content, int commentCnt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.createTime = createTime;
        this.content = content;
        this.commentCnt = commentCnt;
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
