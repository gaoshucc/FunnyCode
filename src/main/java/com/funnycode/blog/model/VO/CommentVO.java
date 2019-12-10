package com.funnycode.blog.model.VO;

import com.funnycode.blog.model.User;

import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-10-03 16:12
 */
public class CommentVO {
    private Long id;
    private User user;
    private String content;
    private Date createTime;
    private List<CommentVO> childComments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<CommentVO> getChildComments() {
        return childComments;
    }

    public void setChildComments(List<CommentVO> childComments) {
        this.childComments = childComments;
    }
}
