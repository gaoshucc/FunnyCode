package com.funnycode.blog.model.VO;

/**
 * @author CC
 * @date 2019-10-31 16:40
 */
public class MessageUserVO {
    private String conversationId;
    private String nickname;
    private String profilePath;
    private String message;
    private Integer unreadCnt;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUnreadCnt() {
        return unreadCnt;
    }

    public void setUnreadCnt(Integer unreadCnt) {
        this.unreadCnt = unreadCnt;
    }
}
