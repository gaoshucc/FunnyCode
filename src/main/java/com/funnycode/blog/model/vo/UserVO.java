package com.funnycode.blog.model.vo;

/**
 * @author CC
 * @date 2019-10-14 10:17
 */
public class UserVO {
    private Long userId;
    private String nickname;
    private String profilePath;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
