package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-10-28 21:07
 */
public class Feedback {
    private Long id;
    private Long userId;
    private String content;

    public Feedback() {
    }

    public Feedback(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
