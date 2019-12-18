package com.funnycode.blog.model.vo;

/**
 * @author CC
 * @date 2019-10-31 22:01
 */
public class PLUserVO {
    private String conversationId;
    private String nickname;
    private String profilePath;

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
}
