package com.funnycode.blog.model.VO;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-07 12:27
 */
public class HomeNoteVO {
    private long id;
    private String title;
    private String type;
    private Date createTime;
    private String content;
    private Double readCnt;
    private Integer commentCnt;
    private Long userId;
    private String authorNickname;

    public HomeNoteVO() {
    }

    public HomeNoteVO(long id, String title, String type, Date createTime, String content, Double readCnt, int commentCnt, long userId, String authorNickname) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.createTime = createTime;
        this.content = content;
        this.readCnt = readCnt;
        this.commentCnt = commentCnt;
        this.userId = userId;
        this.authorNickname = authorNickname;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public Double getReadCnt() {
        return readCnt;
    }

    public void setReadCnt(Double readCnt) {
        this.readCnt = readCnt;
    }
}
