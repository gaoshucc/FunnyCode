package com.funnycode.blog.model.vo;

/**
 * @author CC
 * @date 2019-10-16 13:11
 */
public class BriefNoteVO {
    private Long id;
    private String title;
    private String nickname;

    public BriefNoteVO() {
    }

    public BriefNoteVO(Long id, String title, String nickname) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
