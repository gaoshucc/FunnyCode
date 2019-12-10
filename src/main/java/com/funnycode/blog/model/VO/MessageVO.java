package com.funnycode.blog.model.VO;

import java.util.Date;

/**
 * @author CC
 * @date 2019-10-14 09:46
 */
public class MessageVO {
    private Long id;
    private UserVO fromUser;
    private String content;
    private Date createdDate;
    private Integer hasRead;
    private Integer type;

    public MessageVO() {
    }

    public MessageVO(Long id, UserVO fromUser, String content, Date createdDate, Integer hasRead, Integer type) {
        this.id = id;
        this.fromUser = fromUser;
        this.content = content;
        this.createdDate = createdDate;
        this.hasRead = hasRead;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserVO getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserVO fromUser) {
        this.fromUser = fromUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
